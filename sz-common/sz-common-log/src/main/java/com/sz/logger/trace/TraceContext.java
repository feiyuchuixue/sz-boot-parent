package com.sz.logger.trace;

import com.sz.core.util.HttpReqResUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

/**
 * 当前请求链路上下文。
 */
public final class TraceContext {

    public static final String TRACE_ID = "traceId";

    public static final String TRACE_ID_ATTRIBUTE = TraceContext.class.getName() + ".TRACE_ID";

    private TraceContext() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        try {
            HttpServletRequest request = HttpReqResUtil.getRequest();
            Object value = request.getAttribute(TRACE_ID_ATTRIBUTE);
            return value == null ? "" : String.valueOf(value);
        } catch (Exception ignored) {
            return "";
        }
    }
}
