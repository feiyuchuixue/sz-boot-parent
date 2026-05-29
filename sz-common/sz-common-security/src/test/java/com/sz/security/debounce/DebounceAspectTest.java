package com.sz.security.debounce;

import com.sz.core.common.annotation.Debounce;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.exception.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DebounceAspectTest {

    @Test
    void ignoresGetRequestWhenConfiguredAndNoExplicitDebounceAnnotation() throws Throwable {
        FakeDebounceService debounceService = new FakeDebounceService(true);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("GET"), properties(true, 800, true));
        AtomicInteger proceedCount = new AtomicInteger();

        Object result = aspect.debounceInterceptor(joinPoint(TestController.class.getMethod("list"), new TestController(), proceedCount));

        assertThat(result).isEqualTo("ok");
        assertThat(proceedCount).hasValue(1);
        assertThat(debounceService.acquireCount).isZero();
    }

    @Test
    void explicitDebounceAnnotationStillLocksGetRequestAndUsesMethodLockTime() throws Throwable {
        FakeDebounceService debounceService = new FakeDebounceService(true);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("GET"), properties(true, 800, true));
        AtomicInteger proceedCount = new AtomicInteger();

        Object result = aspect.debounceInterceptor(joinPoint(TestController.class.getMethod("save"), new TestController(), proceedCount));

        assertThat(result).isEqualTo("ok");
        assertThat(proceedCount).hasValue(1);
        assertThat(debounceService.acquireCount).isEqualTo(1);
        assertThat(debounceService.lastLockTime).isEqualTo(2000);
        assertThat(debounceService.lastKey).contains("127.0.0.1:GET:/api/demo:id=1:JUnit");
    }

    @Test
    void throwsDebounceBusinessExceptionWhenLockIsNotAcquired() throws NoSuchMethodException {
        FakeDebounceService debounceService = new FakeDebounceService(false);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("POST"), properties(true, 800, false));
        AtomicInteger proceedCount = new AtomicInteger();

        assertThatThrownBy(() -> aspect.debounceInterceptor(joinPoint(TestController.class.getMethod("list"), new TestController(), proceedCount)))
                .isInstanceOfSatisfying(BusinessException.class, ex -> assertThat(ex.getResponseEnum()).isEqualTo(CommonResponseEnum.DEBOUNCE));

        assertThat(proceedCount).hasValue(0);
        assertThat(debounceService.acquireCount).isEqualTo(1);
        assertThat(debounceService.lastLockTime).isEqualTo(800);
    }

    @Test
    void methodAndClassLevelDebounceIgnoreBypassLock() throws Throwable {
        FakeDebounceService debounceService = new FakeDebounceService(true);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("POST"), properties(true, 800, false));

        aspect.debounceInterceptor(joinPoint(TestController.class.getMethod("ignored"), new TestController(), new AtomicInteger()));
        aspect.debounceInterceptor(joinPoint(IgnoredController.class.getMethod("list"), new IgnoredController(), new AtomicInteger()));

        assertThat(debounceService.acquireCount).isZero();
    }

    @Test
    void classLevelDebounceAppliesToControllerMethods() throws Throwable {
        FakeDebounceService debounceService = new FakeDebounceService(true);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("GET"), properties(true, 800, true));

        aspect.debounceInterceptor(joinPoint(ClassLevelDebounceController.class.getMethod("list"), new ClassLevelDebounceController(), new AtomicInteger()));

        assertThat(debounceService.acquireCount).isEqualTo(1);
        assertThat(debounceService.lastLockTime).isEqualTo(3500);
    }

    @Test
    void disabledDebounceBypassesLock() throws Throwable {
        FakeDebounceService debounceService = new FakeDebounceService(true);
        DebounceAspect aspect = new DebounceAspect(debounceService, request("POST"), properties(false, 800, false));

        aspect.debounceInterceptor(joinPoint(TestController.class.getMethod("list"), new TestController(), new AtomicInteger()));

        assertThat(debounceService.acquireCount).isZero();
    }

    private static DebounceProperties properties(boolean enabled, long globalLockTime, boolean ignoreGetMethod) {
        DebounceProperties properties = new DebounceProperties();
        properties.setEnabled(enabled);
        properties.setGlobalLockTime(globalLockTime);
        properties.setIgnoreGetMethod(ignoreGetMethod);
        return properties;
    }

    private static HttpServletRequest request(String method) {
        return (HttpServletRequest) Proxy.newProxyInstance(DebounceAspectTest.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class},
                (proxy, invokedMethod, args) -> switch (invokedMethod.getName()) {
                    case "getMethod" -> method;
                    case "getRequestURI" -> "/api/demo";
                    case "getQueryString" -> "id=1";
                    case "getRemoteAddr" -> "127.0.0.1";
                    case "getHeader" -> "User-Agent".equals(args[0]) ? "JUnit" : null;
                    default -> defaultValue(invokedMethod.getReturnType());
                });
    }

    private static ProceedingJoinPoint joinPoint(Method method, Object target, AtomicInteger proceedCount) {
        MethodSignature signature = (MethodSignature) Proxy.newProxyInstance(DebounceAspectTest.class.getClassLoader(), new Class<?>[]{MethodSignature.class},
                (proxy, invokedMethod, args) -> switch (invokedMethod.getName()) {
                    case "getMethod" -> method;
                    case "getDeclaringType" -> method.getDeclaringClass();
                    case "getDeclaringTypeName" -> method.getDeclaringClass().getName();
                    default -> defaultValue(invokedMethod.getReturnType());
                });
        return (ProceedingJoinPoint) Proxy.newProxyInstance(DebounceAspectTest.class.getClassLoader(), new Class<?>[]{ProceedingJoinPoint.class},
                (proxy, invokedMethod, args) -> switch (invokedMethod.getName()) {
                    case "getSignature" -> signature;
                    case "getTarget", "getThis" -> target;
                    case "getArgs" -> new Object[0];
                    case "proceed" -> {
                        proceedCount.incrementAndGet();
                        yield "ok";
                    }
                    default -> defaultValue(invokedMethod.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (boolean.class.equals(type)) {
            return false;
        }
        if (char.class.equals(type)) {
            return '\0';
        }
        return 0;
    }

    private static class FakeDebounceService extends RedisDebounceService {

        private final boolean lockResult;

        private int acquireCount;

        private String lastKey;

        private long lastLockTime;

        FakeDebounceService(boolean lockResult) {
            super(null);
            this.lockResult = lockResult;
        }

        @Override
        public boolean acquireLock(String key, long debounceInterval) {
            acquireCount++;
            lastKey = key;
            lastLockTime = debounceInterval;
            return lockResult;
        }
    }

    private static class TestController {

        public void list() {
        }

        @Debounce(time = 2000)
        public void save() {
        }

        @DebounceIgnore
        public void ignored() {
        }
    }

    @DebounceIgnore
    private static class IgnoredController {

        public void list() {
        }
    }

    @Debounce(time = 3500)
    private static class ClassLevelDebounceController {

        public void list() {
        }
    }
}
