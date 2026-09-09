package com.sz.resource.aspect;

import com.sz.core.common.entity.ApiResult;
import com.sz.resource.processor.OssUrlFillProcessor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * OSS URL 自动解析切面。
 *
 * <p>
 * 拦截 {@code @RestController} 方法返回值，将 {@link ApiResult#getData()} 交由
 * {@link OssUrlFillProcessor#process(Object)} 处理。 非 Controller 场景可直接注入
 * {@link OssUrlFillProcessor} 主动调用。
 * </p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OssUrlFillAspect {

    private final OssUrlFillProcessor ossUrlFillProcessor;

    @AfterReturning(pointcut = "@within(org.springframework.web.bind.annotation.RestController)", returning = "result")
    public void fillOssUrl(Object result) {
        if (!(result instanceof ApiResult<?> apiResult)) {
            return;
        }
        ossUrlFillProcessor.process(apiResult.getData());
    }
}
