#!/usr/bin/env bash
set -euo pipefail

max_major="${1:-65}"

if ! [[ "${max_major}" =~ ^[0-9]+$ ]]; then
  echo "[ci][class-version] 最大 class major version 必须是非负整数，实际值: ${max_major}" >&2
  exit 2
fi

scanned=0
highest_major=0
invalid_count=0
too_new_count=0
max_reports=20

while IFS= read -r -d '' class_file; do
  if ! header_bytes=$(od -An -tu1 -N8 "${class_file}"); then
    invalid_count=$((invalid_count + 1))
    if [ "${invalid_count}" -le "${max_reports}" ]; then
      echo "[ci][class-version] 无法读取 class 文件头: ${class_file}" >&2
    fi
    continue
  fi
  read -r magic_1 magic_2 magic_3 magic_4 minor_high minor_low major_high major_low \
    <<< "${header_bytes}"

  if [ "${magic_1:-}" != "202" ] || [ "${magic_2:-}" != "254" ] \
    || [ "${magic_3:-}" != "186" ] || [ "${magic_4:-}" != "190" ] \
    || ! [[ "${minor_high:-}" =~ ^[0-9]+$ ]] || ! [[ "${minor_low:-}" =~ ^[0-9]+$ ]] \
    || ! [[ "${major_high:-}" =~ ^[0-9]+$ ]] || ! [[ "${major_low:-}" =~ ^[0-9]+$ ]]; then
    invalid_count=$((invalid_count + 1))
    if [ "${invalid_count}" -le "${max_reports}" ]; then
      echo "[ci][class-version] 非法 class 文件头: ${class_file}" >&2
    fi
    continue
  fi

  major_version=$((major_high * 256 + major_low))
  scanned=$((scanned + 1))

  if [ "${major_version}" -gt "${highest_major}" ]; then
    highest_major="${major_version}"
  fi

  if [ "${major_version}" -gt "${max_major}" ]; then
    too_new_count=$((too_new_count + 1))
    if [ "${too_new_count}" -le "${max_reports}" ]; then
      echo "[ci][class-version] 超出 Java 21 基线: major=${major_version}, file=${class_file}" >&2
    fi
  fi
done < <(
  find . -type f -name '*.class' \
    \( -path '*/target/classes/*' -o -path '*/target/test-classes/*' \) \
    -print0 | sort -z
)

if [ "${scanned}" -eq 0 ]; then
  echo "[ci][class-version] 未发现 target/classes 或 target/test-classes 下的 class 文件" >&2
  exit 1
fi

if [ "${invalid_count}" -ne 0 ] || [ "${too_new_count}" -ne 0 ]; then
  echo "[ci][class-version] 检查失败: scanned=${scanned}, highest=${highest_major}, allowed=${max_major}, invalid=${invalid_count}, too_new=${too_new_count}" >&2
  exit 1
fi

echo "[ci][class-version] 检查通过: scanned=${scanned}, highest=${highest_major}, allowed=${max_major}"
