package com.sz.core.common.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sz.core.common.annotation.QueryStringArgs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * -- 弃用，后续移除
 * 
 * @author: sz
 * @date: 2022/8/29 10:52
 * @description: 自定义参数解析器 - 用于 GET 请求 对象传参的 蛇形 - 驼峰 转换 -
 *               实现HandlerMethodArgumentResolver 参数解析器，按照我们自定义规则进行参数处理
 */
@Deprecated
public class QueryStringArgsResolver implements HandlerMethodArgumentResolver {

    private ObjectMapper mapper;

    // 使用构造方法传参，或使用SpringApplicationContextUtil从ioc中获取jackson objectMapper对象
    public QueryStringArgsResolver(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 匹配@QueryStringArgs 注解
        if (parameter.hasParameterAnnotation(QueryStringArgs.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = new LinkedHashMap(0);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }
        // 使用jackson配置的规则进行格式化转换
        String json = mapper.writeValueAsString(map);
        Object result = mapper.readValue(json, parameter.getParameterType());
        return result;
    }

}
