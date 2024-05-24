package com.sz.www.test.controller;

import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName 网站测试
 * @Author sz
 * @Date 2024/5/24
 * @Version 1.0
 */
@Tag(name = "测试网站")
@RestController
@RequestMapping("www")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "测试")
    @GetMapping
    public ApiResult<String> test() {
        return ApiPageResult.success("这是一条测试信息");
    }
}
