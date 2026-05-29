package com.sz.logger.trace;

import com.sz.core.util.Utils;
import com.sz.logger.AuditProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 为每个 HTTP 请求准备 traceId，并写入 MDC。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACEPARENT = "traceparent";

    private final AuditProperties auditProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuditProperties.Trace trace = auditProperties.getTrace() == null ? new AuditProperties.Trace() : auditProperties.getTrace();
        if (!trace.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String traceId = resolveTraceId(request, trace);
        request.setAttribute(TraceContext.TRACE_ID_ATTRIBUTE, traceId);
        MDC.put(TraceContext.TRACE_ID, traceId);
        if (trace.isResponseHeaderEnabled()) {
            response.setHeader(trace.getHeaderName(), traceId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceContext.TRACE_ID);
        }
    }

    private String resolveTraceId(HttpServletRequest request, AuditProperties.Trace trace) {
        if (trace.isTraceparentCompatible()) {
            String traceparent = request.getHeader(TRACEPARENT);
            String parsed = parseTraceparent(traceparent);
            if (!parsed.isBlank()) {
                return parsed;
            }
        }
        String traceId = request.getHeader(trace.getHeaderName());
        if (traceId != null && !traceId.isBlank()) {
            return normalize(traceId);
        }
        return Utils.getTraceId();
    }

    private String parseTraceparent(String traceparent) {
        if (traceparent == null || traceparent.isBlank()) {
            return "";
        }
        String[] parts = traceparent.split("-");
        if (parts.length >= 4 && parts[1].matches("[0-9a-fA-F]{32}")) {
            return parts[1].toLowerCase();
        }
        return "";
    }

    private String normalize(String traceId) {
        String value = traceId.trim();
        return value.length() > 64 ? value.substring(0, 64) : value;
    }
}
