package com.sz.logger.aspect;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.core.common.entity.AccessRequestLog;
import com.sz.core.common.entity.AccessResponseLog;
import com.sz.core.util.HttpReqResUtil;
import com.sz.core.util.JsonUtils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Aspect
@Slf4j(topic = "http-log")
@RequiredArgsConstructor
public class AccessLogAspect {

    private final WhitelistProperties whitelistProperties;

    private static final String SEND_TIME = "SEND_TIME";

    private static final long SLOW_QUERY_THRESHOLD = 2000;

    @Pointcut("execution(public * com.sz..*.controller..*.*(..))")
    public void methodArgs() {
    }

    @Before("methodArgs()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            AccessRequestLog requestLog = buildRequestLog(joinPoint, request);
            CopyOnWriteArraySet<String> whitelist = whitelistProperties.getWhitelist();
            if (isNotSaIgnoreInterface(joinPoint) && isNotWhitelist(request.getRequestURI(), request.getContextPath(), whitelist)) {
                requestLog.setUserId(StpUtil.getLoginIdAsString());
            }
            log.info(" [aop] request log : {}", JsonUtils.toJsonString(requestLog));
            request.setAttribute(SEND_TIME, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error in doBefore method", e);
        }
    }

    @AfterReturning(returning = "returnValue", pointcut = "methodArgs()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            AccessResponseLog responseLog = buildResponseLog(joinPoint, returnValue, request);
            CopyOnWriteArraySet<String> whitelist = whitelistProperties.getWhitelist();
            whitelist.add("/auth/logout"); // 登出接口会清除 session，无法获取到用户id。加入白名单中
            if (isNotSaIgnoreInterface(joinPoint) && isNotWhitelist(request.getRequestURI(), request.getContextPath(), whitelist)) {
                responseLog.setUserId(StpUtil.getLoginIdAsString());
            }

            if (responseLog.getMs() >= SLOW_QUERY_THRESHOLD) { // 慢查询日志打印
                log.info(" [aop] response log : {}", JsonUtils.toJsonString(responseLog));
            }
        } catch (Exception e) {
            log.error("Error in doAfterReturning method", e);
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    private AccessRequestLog buildRequestLog(JoinPoint joinPoint, HttpServletRequest request) {
        String contentType = request.getContentType();
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
        String body = HttpReqResUtil.getBody(request);
        Map<String, Object> parameter = HttpReqResUtil.getParameter(request);
        Object[] args = filterAndConvertArguments(joinPoint.getArgs());

        return AccessRequestLog.builder().traceId("").url(request.getRequestURI()).timestamp(System.currentTimeMillis()).method(request.getMethod())
                .ip(HttpReqResUtil.getIpAddress(request)).param(urlParams).body(body).form(parameter).requestBody(args).type("request").contentType(contentType)
                .build();
    }

    private AccessResponseLog buildResponseLog(JoinPoint joinPoint, Object returnValue, HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
        Map<String, Object> parameter = HttpReqResUtil.getParameter(request);
        long sendTime = (long) request.getAttribute(SEND_TIME);
        long ms = System.currentTimeMillis() - sendTime;

        return AccessResponseLog.builder().timestamp(System.currentTimeMillis()).traceId("").param(JsonUtils.toJsonString(urlParams))
                .form(JsonUtils.toJsonString(parameter)).reqBody(filterAndConvertArguments(joinPoint.getArgs())).resBody(returnValue)
                .method(request.getMethod()).url(request.getRequestURI()).ms(ms).build();
    }

    private Object[] filterAndConvertArguments(Object[] args) {
        if (Objects.nonNull(args)) {
            List<Object> filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletResponse || arg instanceof HttpServletRequest || arg instanceof MultipartFile)).toList();
            return filteredArgs.toArray();
        }
        return new Object[0];
    }

    private boolean isNotWhitelist(String requestURI, String contextPath, CopyOnWriteArraySet<String> whitelist) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String pathAfterContext = contextPath.equals("/") ? requestURI.substring(1) : requestURI.replace(contextPath, "");
        return whitelist.stream().noneMatch(pattern -> antPathMatcher.match(pattern, pathAfterContext));
    }

    private boolean isNotSaIgnoreInterface(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(SaIgnore.class);
    }
}