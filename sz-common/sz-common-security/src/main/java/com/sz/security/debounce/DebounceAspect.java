package com.sz.security.debounce;

import com.sz.core.common.annotation.Debounce;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.exception.common.BusinessException;
import com.sz.core.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * 接口防抖切面
 * <p>
 * 拦截 com.sz 包下所有 Controller 方法，根据 {@link DebounceProperties} 配置决定是否启用防抖， 结合
 * {@link Debounce} / {@link DebounceIgnore} 注解支持细粒度控制。 由
 * {@link DebounceAutoConfiguration} 统一注册为 Bean，无需 @Component 自扫描。
 * </p>
 * <p>
 * 防抖命中时统一抛出 {@link BusinessException}（{@link CommonResponseEnum#DEBOUNCE}）， 由
 * {@link com.sz.core.common.exception.GlobalExceptionHandler} 映射为 HTTP 429。
 * </p>
 *
 * @author sz
 * @version 1.0
 * @since 2024/9/18 11:13
 */
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
        Class<?> targetClass = point.getTarget() == null ? method.getDeclaringClass() : point.getTarget().getClass();
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        String httpMethod = request.getMethod();
        Debounce debounce = findDebounce(specificMethod, targetClass);

        // 检查：是否开启了防抖、是否标注了 @DebounceIgnore 注解
        if (!debounceProperties.isEnabled() || hasDebounceIgnore(specificMethod, targetClass)) {
            return point.proceed();
        }

        // 忽略 GET 请求
        if (debounceProperties.isIgnoreGetMethod() && debounce == null && "GET".equalsIgnoreCase(httpMethod)) {
            return point.proceed();
        }

        long lockTime = debounceProperties.getGlobalLockTime();
        if (debounce != null) {
            lockTime = debounce.time();
        }

        String lockKey = Utils.generateDebounceRequestId(request);
        boolean lockAcquired = debounceService.acquireLock(lockKey, lockTime);
        CommonResponseEnum.DEBOUNCE.assertFalse(lockAcquired);
        return point.proceed();
    }

    private static Debounce findDebounce(Method method, Class<?> targetClass) {
        Debounce methodDebounce = AnnotationUtils.findAnnotation(method, Debounce.class);
        return methodDebounce == null ? AnnotationUtils.findAnnotation(targetClass, Debounce.class) : methodDebounce;
    }

    private static boolean hasDebounceIgnore(Method method, Class<?> targetClass) {
        return AnnotationUtils.findAnnotation(method, DebounceIgnore.class) != null
                || AnnotationUtils.findAnnotation(targetClass, DebounceIgnore.class) != null;
    }

}
