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
import org.springframework.core.annotation.Order;

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
        String httpMethod = request.getMethod();
        boolean isDebounceAnno = method.isAnnotationPresent(Debounce.class);

        // 检查：是否开启了防抖、是否标注了 @DebounceIgnore 注解
        if (!debounceProperties.isEnabled() || method.isAnnotationPresent(DebounceIgnore.class)) {
            return point.proceed();
        }

        // 忽略 GET 请求
        if (debounceProperties.isIgnoreGetMethod() && !isDebounceAnno && "GET".equalsIgnoreCase(httpMethod)) {
            return point.proceed();
        }

        long lockTime = debounceProperties.getGlobalLockTime();
        if (isDebounceAnno) {
            Debounce debounce = method.getAnnotation(Debounce.class);
            lockTime = debounce.time();
        }

        String lockKey = Utils.generateDebounceRequestId(request);
        boolean lockAcquired = debounceService.acquireLock(lockKey, lockTime);
        CommonResponseEnum.DEBOUNCE.assertFalse(lockAcquired);
        return point.proceed();
    }

}
