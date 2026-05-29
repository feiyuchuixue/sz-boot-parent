package com.sz.logger.aspect;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.core.common.entity.AccessRequestLog;
import com.sz.core.common.entity.AccessResponseLog;
import com.sz.core.util.HttpReqResUtil;
import com.sz.core.util.JsonUtils;
import com.sz.logger.AuditProperties;
import com.sz.logger.AuditProperties.Access;
import com.sz.logger.AuditProperties.AccessMode;
import com.sz.logger.desensitize.LogDesensitizer;
import com.sz.logger.trace.TraceContext;
import com.sz.security.pojo.WhitelistProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Aspect
@Slf4j(topic = "audit-access-log")
@RequiredArgsConstructor
public class AccessLogAspect {

    private static final String SEND_TIME = "SEND_TIME";

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Set<String> EXTRA_WHITELIST = Set.of("/auth/logout");

    private final WhitelistProperties whitelistProperties;

    private final AuditProperties auditProperties;

    private final LogDesensitizer logDesensitizer;

    @Pointcut("execution(public * com.sz..*.controller..*.*(..))")
    public void methodArgs() {
    }

    @Before("methodArgs()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            Access access = resolveAccessProperties();
            AccessMode mode = resolveAccessMode(access);
            if (mode == AccessMode.OFF) {
                return;
            }

            HttpServletRequest request = HttpReqResUtil.getRequest();
            request.setAttribute(SEND_TIME, System.currentTimeMillis());
            if (mode != AccessMode.FULL) {
                return;
            }

            AccessRequestLog requestLog = buildRequestLog(joinPoint, request, access.isBodyEnabled());
            if (shouldAttachUserId(joinPoint, request)) {
                requestLog.setUserId(StpUtil.getLoginIdAsString());
            }
            log.info("audit.access request {}", logDesensitizer.desensitize(JsonUtils.toJsonString(requestLog)));
        } catch (Exception e) {
            log.error("audit.access request failed", e);
        }
    }

    @AfterReturning(returning = "returnValue", pointcut = "methodArgs()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        try {
            Access access = resolveAccessProperties();
            AccessMode mode = resolveAccessMode(access);
            if (mode == AccessMode.OFF) {
                return;
            }

            HttpServletRequest request = HttpReqResUtil.getRequest();
            long ms = calculateMs(request);
            if (ms < access.getSlowThresholdMs()) {
                return;
            }

            AccessResponseLog responseLog = buildResponseLog(joinPoint, returnValue, request, access.isBodyEnabled(), ms);
            if (shouldAttachUserId(joinPoint, request)) {
                responseLog.setUserId(StpUtil.getLoginIdAsString());
            }
            log.info("audit.access slowResponse {}", logDesensitizer.desensitize(JsonUtils.toJsonString(responseLog)));
        } catch (Exception e) {
            log.error("audit.access response failed", e);
        }
    }

    private AccessRequestLog buildRequestLog(JoinPoint joinPoint, HttpServletRequest request, boolean includeBody) {
        String contentType = request.getContentType();
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
        Map<String, Object> parameter = HttpReqResUtil.getParameter(request);
        String body = includeBody ? HttpReqResUtil.getBody(request) : "";
        Object[] args = includeBody ? filterAndConvertArguments(joinPoint.getArgs()) : new Object[0];

        return AccessRequestLog.builder().traceId(TraceContext.getTraceId()).url(request.getRequestURI()).timestamp(System.currentTimeMillis())
                .method(request.getMethod()).ip(HttpReqResUtil.getIpAddress(request)).param(urlParams).body(body).form(parameter).requestBody(args)
                .type("request").contentType(contentType).build();
    }

    private AccessResponseLog buildResponseLog(JoinPoint joinPoint, Object returnValue, HttpServletRequest request, boolean includeBody, long ms) {
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
        Map<String, Object> parameter = HttpReqResUtil.getParameter(request);

        return AccessResponseLog.builder().timestamp(System.currentTimeMillis()).traceId(TraceContext.getTraceId()).param(JsonUtils.toJsonString(urlParams))
                .form(JsonUtils.toJsonString(parameter)).reqBody(includeBody ? filterAndConvertArguments(joinPoint.getArgs()) : new Object[0])
                .resBody(includeBody ? returnValue : null).method(request.getMethod()).url(request.getRequestURI()).ms(ms).type("response").build();
    }

    private long calculateMs(HttpServletRequest request) {
        Object sendTime = request.getAttribute(SEND_TIME);
        if (sendTime instanceof Long value) {
            return System.currentTimeMillis() - value;
        }
        return 0L;
    }

    private boolean shouldAttachUserId(JoinPoint joinPoint, HttpServletRequest request) {
        CopyOnWriteArraySet<String> whitelist = whitelistProperties.getWhitelist();
        return isNotSaIgnoreInterface(joinPoint) && isNotWhitelist(request.getRequestURI(), request.getContextPath(), whitelist);
    }

    private Object[] filterAndConvertArguments(Object[] args) {
        if (Objects.nonNull(args)) {
            List<Object> filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletResponse || arg instanceof HttpServletRequest || arg instanceof MultipartFile)).toList();
            return filteredArgs.toArray();
        }
        return new Object[0];
    }

    private boolean isNotWhitelist(String requestURI, String contextPath, Set<String> whitelist) {
        String pathAfterContext = resolvePathAfterContext(requestURI, contextPath);
        if (EXTRA_WHITELIST.contains(pathAfterContext)) {
            return false;
        }
        if (whitelist == null || whitelist.isEmpty()) {
            return true;
        }
        return whitelist.stream().noneMatch(pattern -> ANT_PATH_MATCHER.match(pattern, pathAfterContext));
    }

    private String resolvePathAfterContext(String requestURI, String contextPath) {
        String path = requestURI == null ? "" : requestURI;
        if (contextPath != null && !contextPath.isBlank() && !"/".equals(contextPath) && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private Access resolveAccessProperties() {
        return auditProperties.getAccess() == null ? new Access() : auditProperties.getAccess();
    }

    private AccessMode resolveAccessMode(Access access) {
        return access.getMode() == null ? AccessMode.FULL : access.getMode();
    }

    private boolean isNotSaIgnoreInterface(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(SaIgnore.class);
    }
}
