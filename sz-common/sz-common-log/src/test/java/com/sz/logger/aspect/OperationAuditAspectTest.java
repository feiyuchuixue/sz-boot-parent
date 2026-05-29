package com.sz.logger.aspect;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.core.util.SpringApplicationContextUtils;
import com.sz.core.common.entity.ApiResult;
import com.sz.logger.AuditProperties;
import com.sz.logger.audit.BodyRecordMode;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationAuditIgnore;
import com.sz.logger.audit.OperationType;
import com.sz.logger.desensitize.LogDesensitizer;
import com.sz.logger.event.AuditEvent;
import com.sz.logger.event.AuditEventDispatcher;
import com.sz.logger.event.AuditEventSink;
import com.sz.logger.event.AuditEventType;
import com.sz.logger.trace.TraceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationAuditAspectTest {

    @BeforeAll
    static void setUpJsonUtilsContext() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("objectMapper", new ObjectMapper());
        SpringApplicationContextUtils.getInstance().postProcessBeanFactory(beanFactory);
    }

    @AfterEach
    void tearDown() {
        MDC.remove(TraceContext.TRACE_ID);
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void writeRequestBuildsSuccessAuditEventWithMetadataAndMaskedParams() throws Throwable {
        setRequest("POST", "/api/demo", "10.0.0.1");
        MDC.put(TraceContext.TRACE_ID, "trace-1");
        CapturingSink sink = new CapturingSink();
        OperationAuditAspect aspect = aspect(new AuditProperties(), sink);
        DemoController controller = new DemoController();
        DemoDTO dto = new DemoDTO(7L, "abc");

        Object result = aspect.around(joinPoint(DemoController.class.getMethod("create", DemoDTO.class), controller, new Object[]{dto}, ApiResult.success("ok")));

        assertThat(result).isInstanceOf(ApiResult.class);
        assertThat(sink.event).isNotNull();
        assertThat(sink.event.getEventType()).isEqualTo(AuditEventType.OPERATION_SUCCESS);
        assertThat(sink.event.getTraceId()).isEqualTo("trace-1");
        assertThat(sink.event.getModuleName()).isEqualTo("Demo Module");
        assertThat(sink.event.getOperationName()).isEqualTo("Create Demo");
        assertThat(sink.event.getOperationType()).isEqualTo(OperationType.CREATE.name());
        assertThat(sink.event.getPermissionCode()).isEqualTo("demo.create");
        assertThat(sink.event.getBusinessId()).isEqualTo("7");
        assertThat(sink.event.getRequestParams()).contains("\"password\":\"******\"");
        assertThat(sink.event.getRequestParams()).contains("\"id\":7");
        assertThat(sink.event.getIpAddress()).isEqualTo("10.0.0.1");
    }

    @Test
    void explicitGetAuditBypassesDefaultMethodsAndCanRecordResponseBody() throws Throwable {
        setRequest("GET", "/api/demo/7", "127.0.0.1");
        AuditProperties properties = new AuditProperties();
        properties.setRecordResponseBody(true);
        CapturingSink sink = new CapturingSink();
        OperationAuditAspect aspect = aspect(properties, sink);

        aspect.around(joinPoint(DemoController.class.getMethod("get"), new DemoController(), new Object[0], ApiResult.success(new DemoDTO(8L, "secret"))));

        assertThat(sink.event).isNotNull();
        assertThat(sink.event.getOperationType()).isEqualTo(OperationType.QUERY.name());
        assertThat(sink.event.getResponseBody()).contains("\"password\":\"******\"");
    }

    @Test
    void responseBodyNeverAnnotationOverridesGlobalResponseBodySwitch() throws Throwable {
        setRequest("GET", "/api/demo/never", "127.0.0.1");
        AuditProperties properties = new AuditProperties();
        properties.setRecordResponseBody(true);
        CapturingSink sink = new CapturingSink();

        aspect(properties, sink).around(joinPoint(DemoController.class.getMethod("neverRecordResponse"), new DemoController(), new Object[0],
                ApiResult.success(new DemoDTO(8L, "secret"))));

        assertThat(sink.event.getResponseBody()).isEmpty();
    }

    @Test
    void ignoreAnnotationsAndSaIgnoreBypassAudit() throws Throwable {
        setRequest("POST", "/api/demo", "127.0.0.1");
        CapturingSink sink = new CapturingSink();
        OperationAuditAspect aspect = aspect(new AuditProperties(), sink);
        DemoController controller = new DemoController();

        aspect.around(joinPoint(DemoController.class.getMethod("ignored"), controller, new Object[0], ApiResult.success()));
        aspect.around(joinPoint(IgnoredController.class.getMethod("create"), new IgnoredController(), new Object[0], ApiResult.success()));
        aspect.around(joinPoint(DemoController.class.getMethod("publicLogin"), controller, new Object[0], ApiResult.success()));

        assertThat(sink.publishCount).hasValue(0);
    }

    @Test
    void thrownExceptionDispatchesFailureEventAndRethrows() throws NoSuchMethodException {
        setRequest("DELETE", "/api/demo/7", "127.0.0.1");
        CapturingSink sink = new CapturingSink();
        OperationAuditAspect aspect = aspect(new AuditProperties(), sink);
        IllegalArgumentException failure = new IllegalArgumentException("password=secret");

        assertThatThrownBy(() -> aspect.around(joinPoint(DemoController.class.getMethod("delete"), new DemoController(), new Object[0], failure)))
                .isSameAs(failure);

        assertThat(sink.event.getEventType()).isEqualTo(AuditEventType.OPERATION_FAIL);
        assertThat(sink.event.getOperationType()).isEqualTo(OperationType.DELETE.name());
        assertThat(sink.event.getErrorType()).isEqualTo(IllegalArgumentException.class.getName());
        assertThat(sink.event.getErrorMessage()).isEqualTo("password=******");
        assertThat(sink.event.getExceptionStack()).contains("password=******");
    }

    private static OperationAuditAspect aspect(AuditProperties properties, CapturingSink sink) {
        AuditEventDispatcher dispatcher = new AuditEventDispatcher(List.of(sink), properties, new LogDesensitizer());
        return new OperationAuditAspect(properties, new LogDesensitizer(), dispatcher);
    }

    private static void setRequest(String method, String uri, String remoteAddr) {
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(OperationAuditAspectTest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class}, (proxy, invokedMethod, args) -> switch (invokedMethod.getName()) {
                    case "getMethod" -> method;
                    case "getRequestURI" -> uri;
                    case "getRemoteAddr" -> remoteAddr;
                    case "getHeader" -> null;
                    default -> defaultValue(invokedMethod.getReturnType());
                });
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private static ProceedingJoinPoint joinPoint(Method method, Object target, Object[] args, Object resultOrThrowable) {
        MethodSignature signature = (MethodSignature) Proxy.newProxyInstance(OperationAuditAspectTest.class.getClassLoader(),
                new Class<?>[]{MethodSignature.class}, (proxy, invokedMethod, invokedArgs) -> switch (invokedMethod.getName()) {
                    case "getMethod" -> method;
                    case "getDeclaringType" -> method.getDeclaringClass();
                    case "getDeclaringTypeName" -> method.getDeclaringClass().getName();
                    default -> defaultValue(invokedMethod.getReturnType());
                });
        return (ProceedingJoinPoint) Proxy.newProxyInstance(OperationAuditAspectTest.class.getClassLoader(),
                new Class<?>[]{ProceedingJoinPoint.class}, (proxy, invokedMethod, invokedArgs) -> switch (invokedMethod.getName()) {
                    case "getSignature" -> signature;
                    case "getTarget", "getThis" -> target;
                    case "getArgs" -> args;
                    case "proceed" -> {
                        if (resultOrThrowable instanceof Throwable throwable) {
                            throw throwable;
                        }
                        yield resultOrThrowable;
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

    private static class CapturingSink implements AuditEventSink {

        private final AtomicInteger publishCount = new AtomicInteger();

        private AuditEvent event;

        @Override
        public boolean supports(AuditEvent event) {
            return true;
        }

        @Override
        public void publish(AuditEvent event) {
            publishCount.incrementAndGet();
            this.event = event;
        }

        @Override
        public boolean async() {
            return false;
        }
    }

    @Tag(name = "Demo Module")
    private static class DemoController {

        @Operation(summary = "Create Demo")
        @SaCheckPermission("demo.create")
        @OperationAudit(bizId = "#p0.id")
        public ApiResult<String> create(DemoDTO dto) {
            return ApiResult.success("ok");
        }

        @OperationAudit
        public ApiResult<DemoDTO> get() {
            return ApiResult.success(new DemoDTO(8L, "secret"));
        }

        @OperationAudit(responseBody = BodyRecordMode.NEVER)
        public ApiResult<DemoDTO> neverRecordResponse() {
            return ApiResult.success(new DemoDTO(8L, "secret"));
        }

        @OperationAuditIgnore
        public ApiResult<Void> ignored() {
            return ApiResult.success();
        }

        @SaIgnore
        public ApiResult<Void> publicLogin() {
            return ApiResult.success();
        }

        public ApiResult<Void> delete() {
            return ApiResult.success();
        }
    }

    @OperationAuditIgnore
    private static class IgnoredController {

        public ApiResult<Void> create() {
            return ApiResult.success();
        }
    }

    private record DemoDTO(Long id, String password) {
    }
}
