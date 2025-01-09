package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.service.CaptchaService;
import com.sz.core.common.annotation.Debounce;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SliderPuzzle;
import com.sz.core.common.entity.CheckPuzzle;
import com.sz.core.util.SysConfigUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码controller
 *
 * @ClassName CaptchaController
 * @Author sz
 * @Date 2025/01/07 19:07
 * @Version 1.0
 */
@Tag(name = "验证码")
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    @SaIgnore
    @Debounce(time = 1000)
    @GetMapping("status")
    @Operation(summary = "验证码是否启用")
    public ApiResult<Boolean> captchaStatus() {
        String value = SysConfigUtils.getConfValue("sys.captcha.state");
        return ApiResult.success(Boolean.parseBoolean(value));
    }

    @SaIgnore
    @Debounce(time = 1000)
    @Operation(summary = "获取验证码")
    @PostMapping(value = "/get")
    public ApiResult<SliderPuzzle> getImageCode(HttpServletRequest request) {
        return ApiResult.success(captchaService.getImageCode(request));
    }

    @SaIgnore
    @Debounce(time = 1000)
    @Operation(summary = "校验滑块拼图验证码")
    @PostMapping(value = "/check")
    public ApiResult captchaCheck(@RequestBody CheckPuzzle checkPuzzle) throws Exception {
        captchaService.checkImageCode(checkPuzzle);
        return ApiResult.success();
    }

}
