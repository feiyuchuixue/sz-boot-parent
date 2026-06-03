package com.sz.security.core.interceptor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.sz.core.common.entity.ControlPermissions;
import com.sz.core.datascope.ControlThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

/**
 * @author sz
 * @since 2024/7/9 14:29
 */
public class MySaInterceptor extends SaInterceptor {

    public MySaInterceptor() {
    }

    public MySaInterceptor(SaParamFunction<Object> auth) {
        super(auth);
    }

    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
            if (isAnnotation && handler instanceof HandlerMethod) {
                Method method = ((HandlerMethod) handler).getMethod();
                SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);

                // 如果此 Method 标注了 @SaCheckPermission，则进行（数据权限）校验
                SaCheckPermission checkPermission = (SaCheckPermission) SaAnnotationStrategy.instance.getAnnotation.apply(method, SaCheckPermission.class);
                if (checkPermission != null) {
                    ControlThreadLocal.set(new ControlPermissions(checkPermission.value()));
                }
            }

            // Auth 校验
            auth.run(handler);

        } catch (StopMatchException e) {
            // 停止匹配，正常进入 Controller，ControlPermissions 由 afterCompletion 统一清理
        } catch (BackResultException e) {
            ControlThreadLocal.clearDataScope();
            // BackResultException 异常代表：停止匹配，向前端输出结果
            // 统一输出 JSON 格式，确保前端拦截器可正常解析
            response.setContentType("application/json;charset=UTF-8");
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
