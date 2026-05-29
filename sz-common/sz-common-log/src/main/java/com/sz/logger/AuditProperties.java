package com.sz.logger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 审计日志配置，绑定 {@code sz.audit}。
 * <p>
 * 配置来源为 {@code config/{profile}/audit-log.yml}，修改后通过重启服务生效。 本配置不依赖
 * {@code sys_config}，避免审计能力绑定数据库、缓存等运行时依赖。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.audit")
public class AuditProperties {

    /**
     * SQL 审计日志配置。
     */
    private Sql sql = new Sql();

    /**
     * HTTP 访问审计日志配置。
     */
    private Access access = new Access();

    /**
     * 操作审计配置，用于记录“谁在什么时间执行了什么操作”。
     */
    private Operation operation = new Operation();

    /**
     * 性能、异常和响应内容等诊断明细配置。
     */
    private Diagnostic diagnostic = new Diagnostic();

    /**
     * 链路追踪配置，用于在文件日志和审计记录中串联同一次请求。
     */
    private Trace trace = new Trace();

    /**
     * 外部审计事件发布配置，用于后续扩展邮件、钉钉、Webhook、MQ 等通知体系。
     * <p>
     * 该配置只控制 Spring 事件扩展，不影响核心操作审计入库和文件兜底。
     */
    private Event event = new Event();

    /**
     * SQL 审计输出模式。
     */
    public enum SqlMode {
        /**
         * 打印全部 SQL。
         */
        ALL,
        /**
         * 仅打印超过慢 SQL 阈值的 SQL。
         */
        SLOW,
        /**
         * 关闭 MyBatis-Flex SQL audit。
         */
        OFF
    }

    /**
     * HTTP 访问审计输出模式。
     */
    public enum AccessMode {
        /**
         * 打印请求摘要，并打印超过慢请求阈值的响应日志。
         */
        FULL,
        /**
         * 仅打印超过慢请求阈值的响应日志。
         */
        SLOW,
        /**
         * 关闭 HTTP 访问审计日志。
         */
        OFF
    }

    /**
     * 操作审计主记录写入模式。
     */
    public enum WriteMode {
        /**
         * 同步尽力写入主审计记录，失败不阻断业务。
         */
        SYNC,
        /**
         * 异步写入主审计记录。当前默认不使用，保留给高吞吐场景。
         */
        ASYNC
    }

    /**
     * SQL 审计日志参数。
     */
    @Data
    public static class Sql {

        /**
         * 输出模式，默认打印全部 SQL。
         */
        private SqlMode mode = SqlMode.ALL;

        /**
         * 慢 SQL 阈值，单位毫秒。仅 {@link SqlMode#SLOW} 模式生效。
         */
        private long slowThresholdMs = 1000L;

        /**
         * 是否打印完整 SQL。关闭后仅输出耗时、行数等摘要信息。
         */
        private boolean fullSqlEnabled = true;
    }

    /**
     * HTTP 访问审计日志参数。
     */
    @Data
    public static class Access {

        /**
         * 输出模式，默认打印请求摘要和慢响应日志。
         */
        private AccessMode mode = AccessMode.FULL;

        /**
         * 慢请求阈值，单位毫秒。
         */
        private long slowThresholdMs = 2000L;

        /**
         * 是否记录请求和响应 body。默认关闭，避免敏感数据和大 body 进入日志。
         */
        private boolean bodyEnabled = false;
    }

    /**
     * 操作审计主记录参数。
     */
    @Data
    public static class Operation {

        /**
         * 是否启用操作审计。默认开启。
         */
        private boolean enabled = true;

        /**
         * 主审计记录写入模式。默认同步尽力写入，保存失败不阻断业务。
         */
        private WriteMode writeMode = WriteMode.SYNC;

        /**
         * 默认自动采集的 HTTP 方法。显式 {@code @OperationAudit} 不受该列表限制。
         */
        private Set<String> methods = new LinkedHashSet<>(Set.of("POST", "PUT", "DELETE", "PATCH"));

        /**
         * 慢操作阈值，单位毫秒。超过该阈值会标记为慢操作，并触发性能诊断明细。
         */
        private long slowThresholdMs = 2000L;

        /**
         * 是否记录请求参数明细。默认开启，写操作会保存脱敏、截断后的参数快照。
         */
        private boolean paramsEnabled = true;

        /**
         * 是否记录响应内容。默认关闭，开启后仍会先脱敏再截断。
         */
        private boolean responseBodyEnabled = false;

        /**
         * 请求参数最大长度，超过后截断。
         */
        private int requestParamsMaxLength = 4000;

        /**
         * 响应内容最大长度，超过后截断。
         */
        private int responseBodyMaxLength = 4000;

        /**
         * 普通摘要字段最大长度，避免 URI、异常消息、权限码等字段过长。
         */
        private int maxFieldLength = 500;

        public boolean containsMethod(String method) {
            return method != null && methods.stream().anyMatch(item -> method.equalsIgnoreCase(item));
        }
    }

    /**
     * 性能、异常和响应内容等诊断明细参数。
     */
    @Data
    public static class Diagnostic {

        /**
         * 是否启用诊断明细异步保存。
         */
        private boolean enabled = true;

        /**
         * 是否保存慢操作诊断明细。
         */
        private boolean performanceEnabled = true;

        /**
         * 是否保存异常诊断明细。
         */
        private boolean exceptionEnabled = true;

        /**
         * 诊断异步队列容量。队列满时丢弃诊断明细并写文件日志。
         */
        private int asyncQueueCapacity = 1000;

        /**
         * 诊断异步线程池核心线程数。
         */
        private int asyncCoreSize = 2;

        /**
         * 诊断异步线程池最大线程数。
         */
        private int asyncMaxSize = 4;
    }

    /**
     * 链路追踪参数。
     */
    @Data
    public static class Trace {

        /**
         * 是否启用轻量链路追踪。
         */
        private boolean enabled = true;

        /**
         * 非 W3C 场景下使用的 trace 请求头名称。
         */
        private String headerName = "X-Trace-Id";

        /**
         * 是否把 traceId 回写到响应头，方便前端和网关定位。
         */
        private boolean responseHeaderEnabled = true;

        /**
         * 是否兼容 W3C traceparent 请求头。
         */
        private boolean traceparentCompatible = true;
    }

    /**
     * 审计事件发布参数。
     */
    @Data
    public static class Event {

        /**
         * 是否发布外部审计事件。关闭后仅停止 Spring 事件扩展，操作审计入库和文件兜底仍然生效。
         */
        private boolean enabled = true;

        /**
         * 内置 Spring 事件发布 Sink 的投递模式。默认异步，避免外部监听逻辑影响业务请求。
         */
        private WriteMode publishMode = WriteMode.ASYNC;

        /**
         * 外部审计事件是否携带请求参数、响应体、异常堆栈等明细。默认关闭。
         */
        private boolean includeDetail = false;
    }
}
