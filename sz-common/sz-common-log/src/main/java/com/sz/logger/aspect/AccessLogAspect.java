package com.sz.logger.aspect;

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
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TODO: 优化request参数和response参数。 最终结合SkyWalking使用再做改造 Controller请求切面 打印请求参数
 *
 * @author sz
 * @date 2022/2/17 9:11
 */
@Component
@Aspect
@Slf4j(topic = "http-log")
@RequiredArgsConstructor
public class AccessLogAspect {

    private final WhitelistProperties whitelistProperties;

    private static final String SEND_TIME = "SEND_TIME";

    @Pointcut("(execution(public * com.sz..*.controller..*.*(..)))")
    public void methodArgs() {
    }

    @Before("methodArgs()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String contentType = request.getContentType();
            // GET请求地址栏参数
            String queryString = request.getQueryString();
            Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
            // POST请求获取body体
            String body = HttpReqResUtil.getBody(request);
            // FORM表单参数
            Map<String, Object> parameter = HttpReqResUtil.getParameter(request);
            if (Arrays.stream(joinPoint.getArgs()).anyMatch(arg -> arg instanceof MultipartFile)) {
                // 忽略包含MultipartFile的请求
                return;
            }

            String requestURI = request.getRequestURI();
            Object[] args = joinPoint.getArgs();
            filterAndConvertArguments(args);

            AccessRequestLog requestLog = AccessRequestLog.builder().traceId("").url(request.getRequestURI()).timestamp(System.currentTimeMillis())
                    .method(request.getMethod()).ip(HttpReqResUtil.getIpAddress(request)).param(urlParams).body(body).form(parameter).requestBody(args)
                    .type("request").contentType(contentType).build();
            List<String> whitelist = whitelistProperties.getWhitelist();
            if (!isWhitelist(requestURI, request.getContextPath(), whitelist)) {
                requestLog.setUserId(StpUtil.getLoginIdAsString());
            }
            log.info(" [aop] request log : {}", JsonUtils.toJsonString(requestLog));
            // 设置请求开始时间
            request.setAttribute(SEND_TIME, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error in doBefore method", e);
        }
    }

    @AfterReturning(returning = "returnValue", pointcut = "methodArgs()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (Arrays.stream(joinPoint.getArgs()).anyMatch(arg -> arg instanceof MultipartFile)) {
                // 忽略包含MultipartFile的请求
                return;
            }

            // GET请求地址栏参数
            String queryString = request.getQueryString();
            Map<String, Object> urlParams = HttpReqResUtil.getUrlParams(queryString);
            // FORM表单参数
            Map<String, Object> parameter = HttpReqResUtil.getParameter(request);
            long sendTime = (long) request.getAttribute(SEND_TIME);
            long ms = System.currentTimeMillis() - sendTime;
            AccessResponseLog responseLog = AccessResponseLog.builder().timestamp(System.currentTimeMillis()).traceId("")
                    .param(JsonUtils.toJsonString(urlParams)).form(JsonUtils.toJsonString(parameter)).reqBody(joinPoint.getArgs()).resBody(returnValue)
                    .method(request.getMethod()).url(request.getRequestURI()).ms(ms).build();
            String requestURI = request.getRequestURI();
            List<String> whitelist = whitelistProperties.getWhitelist();
            whitelist.add("/auth/logout"); // 退出登录接口也需要排除掉，因为logout的service方法先执行，在切面获取userId时已经失效了。
            if (!isWhitelist(requestURI, request.getContextPath(), whitelist)) {
                responseLog.setUserId(StpUtil.getLoginIdAsString());
            }

            // 慢查询打印，大于3s的接口
            if (ms >= 2000) {
                log.info(" [aop] response log : {}", JsonUtils.toJsonString(responseLog));
            }
        } catch (Exception e) {
            log.error("Error in doAfterReturning method", e);
        }
    }

    private static void filterAndConvertArguments(Object[] args) {
        if (Objects.nonNull(args)) {
            List<Object> argsList = Arrays.asList(args);
            // 将 HttpServletResponse 和 HttpServletRequest 参数移除，不然会报异常
            List<Object> collect = argsList.stream().filter(o -> !(o instanceof HttpServletResponse || o instanceof HttpServletRequest))
                    .collect(Collectors.toList());
            collect.toArray(args);
        }
    }

    private boolean isWhitelist(String requestURI, String contextPath, List<String> whitelist) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 替换上下文路径及其后面的斜杠（如果有）
        String pathAfterContext;
        // 如果上下文路径是根路径（"/"），则替换可能会导致问题，需要特殊处理
        if (contextPath.equals("/")) {
            pathAfterContext = requestURI.substring(1);
        } else {
            pathAfterContext = requestURI.replace(contextPath, "");
        }
        return whitelist.stream().anyMatch(pattern -> antPathMatcher.match(pattern, pathAfterContext));
    }

}
