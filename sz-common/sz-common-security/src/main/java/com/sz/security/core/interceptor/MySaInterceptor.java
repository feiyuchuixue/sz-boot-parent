package com.sz.security.core.interceptor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaStrategy;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.datascope.ControlThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

/**
 * @ClassName MySaInterceptor
 * @Author sz
 * @Date 2024/7/9 14:29
 * @Version 1.0
 */
public class MySaInterceptor extends SaInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {

            // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
            if (isAnnotation && handler instanceof HandlerMethod) {

                // 获取此请求对应的 Method 处理函数
                Method method = ((HandlerMethod) handler).getMethod();


                // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
                if (SaStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class)) {
                    // 注意这里直接就退出整个鉴权了，最底部的 auth.run() 路由拦截鉴权也被跳出了
                    return true;
                }

                // 如果此 Method 标注了 @SaCheckPermission，则进行权限校验
                SaCheckPermission checkPermission = (SaCheckPermission) SaStrategy.instance.getAnnotation.apply(method, SaCheckPermission.class);
                if (checkPermission != null) {
                    ControlThreadLocal.set(new ControlPermissions(checkPermission.value(), checkPermission.mode().name()));
                }

                // 注解校验
                SaStrategy.instance.checkMethodAnnotation.accept(method);
            }

            // Auth 校验
            auth.run(handler);

        } catch (StopMatchException e) {
            // StopMatchException 异常代表：停止匹配，进入Controller

        } catch (BackResultException e) {
            // BackResultException 异常代表：停止匹配，向前端输出结果
            // 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 back 前自行设置 Content-Type 为 application/json
            // 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
            if (response.getContentType() == null) {
                response.setContentType("text/plain; charset=utf-8");
            }
            response.getWriter().print(e.getMessage());
            return false;
        }

        // 通过验证
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ControlThreadLocal.clearDataScope();
        super.afterCompletion(request, response, handler, ex);
    }
}
