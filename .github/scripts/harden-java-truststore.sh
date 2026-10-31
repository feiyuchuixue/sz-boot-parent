#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPOSITORY_DENYLIST="${SCRIPT_DIR}/../security/java-truststore-denylist.txt"
IMAGE_DENYLIST='/usr/local/share/sz-security/java-truststore-denylist.txt'
STOREPASS='changeit'

usage() {
  cat >&2 <<'EOF'
Usage: harden-java-truststore.sh (--apply|--check) [--keystore PATH] [--denylist PATH]
EOF
  exit 2
}

log() {
  echo "[java-truststore] $1"
}

fail() {
  echo "[java-truststore] $1" >&2
  exit 1
}

mode=''
keystore=''
denylist=''

while [ "$#" -gt 0 ]; do
  case "$1" in
    --apply|--check)
      [ -z "${mode}" ] || usage
      mode=$1
      shift
      ;;
    --keystore)
      [ "$#" -ge 2 ] || usage
      keystore=$2
      shift 2
      ;;
    --denylist)
      [ "$#" -ge 2 ] || usage
      denylist=$2
      shift 2
      ;;
    *)
      usage
      ;;
  esac
done

[ -n "${mode}" ] || usage

if [ -n "${KEYTOOL_BIN:-}" ]; then
  keytool_bin=${KEYTOOL_BIN}
elif [ -n "${JAVA_HOME:-}" ]; then
  keytool_bin="${JAVA_HOME}/bin/keytool"
else
  keytool_bin=$(command -v keytool || true)
fi
[ -x "${keytool_bin}" ] || fail '找不到可执行 keytool'

if [ -z "${keystore}" ]; then
  [ -n "${JAVA_HOME:-}" ] || fail '未指定 --keystore 且 JAVA_HOME 为空'
  keystore="${JAVA_HOME}/lib/security/cacerts"
fi
[ -f "${keystore}" ] || fail "Java truststore 不存在: ${keystore}"
[ -r "${keystore}" ] || fail "Java truststore 不可读: ${keystore}"
if [ "${mode}" = '--apply' ]; then
  [ -w "${keystore}" ] || fail "Java truststore 不可写: ${keystore}"
fi

if [ -z "${denylist}" ]; then
  if [ -n "${JAVA_TRUSTSTORE_DENYLIST:-}" ]; then
    denylist=${JAVA_TRUSTSTORE_DENYLIST}
  elif [ -f "${REPOSITORY_DENYLIST}" ]; then
    denylist=${REPOSITORY_DENYLIST}
  else
    denylist=${IMAGE_DENYLIST}
  fi
fi
[ -f "${denylist}" ] || fail "Java truststore 拒绝策略不存在: ${denylist}"
[ -r "${denylist}" ] || fail "Java truststore 拒绝策略不可读: ${denylist}"

if ! "${keytool_bin}" -list -keystore "${keystore}" -storepass "${STOREPASS}" >/dev/null 2>&1; then
  fail "无法使用预期密码读取 Java truststore: ${keystore}"
fi

alias_exists() {
  local alias=$1
  local output

  if ! output=$("${keytool_bin}" -list -keystore "${keystore}" \
    -storepass "${STOREPASS}" 2>&1); then
    echo "[java-truststore] 无法检查 Java truststore alias: ${alias}" >&2
    return 2
  fi

  if grep -Fq "${alias}," <<< "${output}"; then
    return 0
  fi
  return 1
}

check_policy() {
  local raw alias
  local violations=0

  while IFS= read -r raw || [ -n "${raw}" ]; do
    alias=$(printf '%s' "${raw}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
    case "${alias}" in
      ''|'#'*) continue ;;
    esac

    if alias_exists "${alias}"; then
      echo "[java-truststore] 禁止信任的证书仍存在: ${alias}" >&2
      violations=$((violations + 1))
    else
      alias_status=$?
      [ "${alias_status}" -eq 1 ] || return 1
    fi
  done < "${denylist}"

  if [ "${violations}" -ne 0 ]; then
    echo "[java-truststore] 安全检查失败，命中 ${violations} 个禁止 alias" >&2
    return 1
  fi

  log '安全检查通过，禁止 alias 数量为 0'
}

if [ "${mode}" = '--apply' ]; then
  while IFS= read -r raw || [ -n "${raw}" ]; do
    alias=$(printf '%s' "${raw}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
    case "${alias}" in
      ''|'#'*) continue ;;
    esac

    if alias_exists "${alias}"; then
      log "删除禁止信任的证书: ${alias}"
      "${keytool_bin}" -delete -keystore "${keystore}" -storepass "${STOREPASS}" \
        -alias "${alias}" >/dev/null
    else
      alias_status=$?
      [ "${alias_status}" -eq 1 ] || exit 1
    fi
  done < "${denylist}"
fi

check_policy
