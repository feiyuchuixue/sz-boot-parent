package com.sz.platform.debounce;

import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.annotation.Debounce;
import com.sz.core.common.annotation.DebounceIgnore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 接口防抖切面
 *
 * @ClassName DebounceAspect
 * @Author sz
 * @Date 2024/9/18 11:13
 * @Version 1.0
 */
@Component
@Aspect
@Order(value = Integer.MIN_VALUE)
@RequiredArgsConstructor
public class DebounceAspect {
    private final RedisDebounceService debounceService;

    private final HttpServletRequest request;

    private final DebounceProperties debounceProperties;

    @Pointcut("(execution(* com.sz..*Controller.*(..)))")
    public void methodArgs() {
    }

    @Around("methodArgs()")
    public Object debounceInterceptor(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        // 检查：是否开启了防抖、是否标注了 @NoDebounce 注解
        if (!debounceProperties.isEnabled() || method.isAnnotationPresent(DebounceIgnore.class)) {
            return point.proceed(); // 直接执行，不做防抖处理
        }
        long lockTime = debounceProperties.getGlobalLockTime();

        if (method.isAnnotationPresent(Debounce.class)) {
            Debounce debounce = method.getAnnotation(Debounce.class);
            lockTime = debounce.time();
        }

        String lockKey = generateLockKey(request);

        // 尝试获取分布式锁
        boolean lockAcquired = debounceService.acquireLock(lockKey, lockTime);
        if (!lockAcquired) {
            // 锁获取失败，返回防抖提示
            if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
                return CompletableFuture.completedFuture("请求过于频繁，请稍后再试。"); // 异步返回结构问题，待验证。
            } else {
                // 获取返回类型
                Class<?> returnType = method.getReturnType();

                // 如果返回类型是 ApiPageResult
                if (returnType.isAssignableFrom(ApiPageResult.class)) {
                    return ApiPageResult.error(CommonResponseEnum.DEBOUNCE);  // 返回 ApiPageResult
                }
                // 如果返回类型是 ApiResult
                else if (returnType.isAssignableFrom(ApiResult.class)) {
                    return ApiResult.error(CommonResponseEnum.DEBOUNCE);  // 返回 ApiResult
                }
                // 如果返回类型是其他类型，比如 String
                else if (returnType.isAssignableFrom(String.class)) {
                    return "请求过于频繁，请稍后再试。";  // 返回简单的字符串
                }
                // 如果是其他类型，抛出异常或者做其他处理
                else {
                    throw new IllegalStateException("无法处理的返回类型：" + method.getReturnType().getName());
                }
            }
        }

        Object result = point.proceed(); // 执行方法
        if (result instanceof CompletableFuture) {
            return ((CompletableFuture<String>) result)
                    .exceptionally(ex -> "执行过程中发生异常：" + ex.getMessage());
        }
        return result;
    }

    private String generateLockKey(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String queryString = request.getQueryString();
        return ip + ":" + method + ": " + uri + ":" + queryString + ":" + userAgent;
    }

}
