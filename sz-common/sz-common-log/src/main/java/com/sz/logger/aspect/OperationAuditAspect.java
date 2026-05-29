package com.sz.logger.aspect;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.util.HttpReqResUtil;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.Utils;
import com.sz.logger.AuditProperties;
import com.sz.logger.audit.BodyRecordMode;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationAuditIgnore;
import com.sz.logger.audit.OperationType;
import com.sz.logger.desensitize.LogDesensitizer;
import com.sz.logger.event.AuditEvent;
import com.sz.logger.event.AuditEventDispatcher;
import com.sz.logger.event.AuditEventType;
import com.sz.logger.trace.TraceContext;
import com.sz.security.core.util.LoginUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 操作审计切面。
 */
@Aspect
@Component
@Slf4j(topic = "diagnostic-exception-log")
@RequiredArgsConstructor
public class OperationAuditAspect {

    private static final String SUCCESS_CODE = "0000";

    private final AuditProperties auditProperties;

    private final LogDesensitizer logDesensitizer;

    private final AuditEventDispatcher auditEventDispatcher;

    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("execution(public * com.sz..*.controller..*.*(..))")
    public void controllerMethod() {
    }

    @Around("controllerMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        AuditProperties.Operation operation = resolveOperationProperties();
        if (!operation.isEnabled()) {
            return joinPoint.proceed();
        }

        Method signatureMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
        Method method = AopUtils.getMostSpecificMethod(signatureMethod, targetClass);
        OperationAudit audit = resolveOperationAudit(method, targetClass);
        if (!shouldAudit(method, targetClass, audit, operation)) {
            return joinPoint.proceed();
        }

        long start = System.currentTimeMillis();
        try {
            Object returnValue = joinPoint.proceed();
            long costMs = System.currentTimeMillis() - start;
            AuditEvent event = buildEvent(joinPoint, method, targetClass, audit, returnValue, null, costMs, operation);
            auditEventDispatcher.dispatch(event);
            return returnValue;
        } catch (Throwable ex) {
            long costMs = System.currentTimeMillis() - start;
            AuditEvent event = buildEvent(joinPoint, method, targetClass, audit, null, ex, costMs, operation);
            auditEventDispatcher.dispatch(event);
            throw ex;
        }
    }

    private AuditEvent buildEvent(ProceedingJoinPoint joinPoint, Method method, Class<?> targetClass, OperationAudit audit, Object returnValue,
            Throwable throwable, long costMs, AuditProperties.Operation operation) {
        HttpServletRequest request = HttpReqResUtil.getRequest();
        ApiResult<?> apiResult = returnValue instanceof ApiResult<?> result ? result : null;
        boolean success = throwable == null && (apiResult == null || SUCCESS_CODE.equals(apiResult.getCode()));
        String errorType = throwable == null ? "" : throwable.getClass().getName();
        String errorMessage = throwable == null ? "" : throwable.getMessage();
        String responseCode = apiResult == null ? "" : apiResult.getCode();
        String responseMessage = apiResult == null ? "" : apiResult.getMessage();
        String methodName = request.getMethod();
        String requestParams = resolveRequestParams(joinPoint, method, audit, operation);
        String responseBody = resolveResponseBody(returnValue, audit, operation);
        String exceptionStack = throwable == null ? "" : stackTraceToString(throwable);

        return AuditEvent.builder().eventId(Utils.getTraceId()).eventType(success ? AuditEventType.OPERATION_SUCCESS : AuditEventType.OPERATION_FAIL)
                .traceId(TraceContext.getTraceId()).moduleName(truncate(resolveModuleName(targetClass, audit), operation.getMaxFieldLength()))
                .operationName(truncate(resolveOperationName(method, audit), operation.getMaxFieldLength()))
                .operationType(resolveOperationType(methodName, audit).name()).status(success ? "SUCCESS" : "FAIL").userId(resolveUserId())
                .userName(resolveUserName()).permissionCode(truncate(resolvePermissionCode(method), operation.getMaxFieldLength())).requestMethod(methodName)
                .requestUri(truncate(request.getRequestURI(), operation.getMaxFieldLength()))
                .businessId(truncate(resolveBusinessId(joinPoint, method, audit), 128)).ipAddress(truncate(HttpReqResUtil.getIpAddress(request), 64))
                .costMs(costMs).slow(costMs >= operation.getSlowThresholdMs()).responseCode(truncate(responseCode, 64))
                .responseMessage(truncate(responseMessage, operation.getMaxFieldLength())).errorType(truncate(errorType, operation.getMaxFieldLength()))
                .errorMessage(truncate(logDesensitizer.desensitize(errorMessage), operation.getMaxFieldLength())).requestParams(requestParams)
                .responseBody(responseBody).exceptionStack(logDesensitizer.desensitize(exceptionStack)).occurredAt(LocalDateTime.now()).build();
    }

    private boolean shouldAudit(Method method, Class<?> targetClass, OperationAudit audit, AuditProperties.Operation operation) {
        if (method.isAnnotationPresent(OperationAuditIgnore.class) || targetClass.isAnnotationPresent(OperationAuditIgnore.class)) {
            return false;
        }
        if (audit != null) {
            return true;
        }
        if (method.isAnnotationPresent(SaIgnore.class) || targetClass.isAnnotationPresent(SaIgnore.class)) {
            return false;
        }
        HttpServletRequest request = HttpReqResUtil.getRequest();
        return operation.containsMethod(request.getMethod());
    }

    private OperationAudit resolveOperationAudit(Method method, Class<?> targetClass) {
        OperationAudit methodAudit = method.getAnnotation(OperationAudit.class);
        if (methodAudit != null) {
            return methodAudit;
        }
        return targetClass.getAnnotation(OperationAudit.class);
    }

    private String resolveModuleName(Class<?> targetClass, OperationAudit audit) {
        if (audit != null && !audit.module().isBlank()) {
            return audit.module();
        }
        Tag tag = targetClass.getAnnotation(Tag.class);
        return tag == null ? targetClass.getSimpleName() : tag.name();
    }

    private String resolveOperationName(Method method, OperationAudit audit) {
        if (audit != null && !audit.name().isBlank()) {
            return audit.name();
        }
        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null && !operation.summary().isBlank()) {
            return operation.summary();
        }
        return method.getName();
    }

    private OperationType resolveOperationType(String httpMethod, OperationAudit audit) {
        if (audit != null && audit.operationType() != OperationType.AUTO) {
            return audit.operationType();
        }
        return switch (httpMethod.toUpperCase()) {
            case "POST" -> OperationType.CREATE;
            case "PUT", "PATCH" -> OperationType.UPDATE;
            case "DELETE" -> OperationType.DELETE;
            case "GET" -> OperationType.QUERY;
            default -> OperationType.OTHER;
        };
    }

    private String resolvePermissionCode(Method method) {
        SaCheckPermission permission = method.getAnnotation(SaCheckPermission.class);
        if (permission == null || permission.value().length == 0) {
            return "";
        }
        return String.join(",", permission.value());
    }

    private String resolveBusinessId(ProceedingJoinPoint joinPoint, Method method, OperationAudit audit) {
        if (audit == null || audit.bizId().isBlank()) {
            return "";
        }
        try {
            EvaluationContext context = new StandardEvaluationContext();
            Object[] args = joinPoint.getArgs();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            for (int i = 0; i < args.length; i++) {
                context.setVariable("p" + i, args[i]);
                context.setVariable("a" + i, args[i]);
                if (parameterNames != null && i < parameterNames.length) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            Object value = expressionParser.parseExpression(audit.bizId()).getValue(context);
            return formatBusinessId(value);
        } catch (Exception e) {
            log.warn("audit.operation bizId parse failed expression={} error={}", audit.bizId(), e.getMessage());
            return "";
        }
    }

    private String formatBusinessId(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            return IntStream.range(0, Array.getLength(value)).mapToObj(index -> String.valueOf(Array.get(value, index))).collect(Collectors.joining(","));
        }
        return String.valueOf(value);
    }

    private String resolveRequestParams(ProceedingJoinPoint joinPoint, Method method, OperationAudit audit, AuditProperties.Operation operation) {
        boolean enabled = operation.isParamsEnabled() || (audit != null && audit.recordParams());
        if (!enabled) {
            return "";
        }
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (!isSerializableArgument(arg)) {
                continue;
            }
            String name = parameterNames != null && i < parameterNames.length && parameterNames[i] != null && !parameterNames[i].isBlank()
                    ? parameterNames[i]
                    : "arg" + i;
            params.put(name, arg);
        }
        if (params.isEmpty()) {
            return "";
        }
        return truncate(logDesensitizer.desensitize(toJsonString(params)), operation.getRequestParamsMaxLength());
    }

    private String resolveResponseBody(Object returnValue, OperationAudit audit, AuditProperties.Operation operation) {
        if (!operation.isResponseBodyEnabled()) {
            return "";
        }
        if (audit != null && audit.responseBody() == BodyRecordMode.NEVER) {
            return "";
        }
        if (returnValue == null || returnValue instanceof HttpServletResponse) {
            return "";
        }
        return truncate(logDesensitizer.desensitize(toJsonString(returnValue)), operation.getResponseBodyMaxLength());
    }

    private boolean isSerializableArgument(Object arg) {
        return !(arg == null || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof MultipartFile);
    }

    private String resolveUserId() {
        try {
            return StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private String resolveUserName() {
        try {
            LoginUser loginUser = LoginUtils.getLoginUser();
            if (loginUser == null || loginUser.getUserInfo() == null) {
                return "";
            }
            String nickname = loginUser.getUserInfo().getNickname();
            return nickname == null || nickname.isBlank() ? loginUser.getUserInfo().getUsername() : nickname;
        } catch (Exception ignored) {
            return "";
        }
    }

    private String toJsonString(Object value) {
        try {
            return JsonUtils.toJsonString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private String stackTraceToString(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (maxLength <= 0 || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private AuditProperties.Operation resolveOperationProperties() {
        return auditProperties.getOperation() == null ? new AuditProperties.Operation() : auditProperties.getOperation();
    }
}
