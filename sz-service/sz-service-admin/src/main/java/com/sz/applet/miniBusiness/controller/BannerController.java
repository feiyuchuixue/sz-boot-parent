package com.sz.applet.miniBusiness.controller;

import com.sz.applet.miniBusiness.pojo.dto.BannerCreateDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerListDTO;
import com.sz.applet.miniBusiness.pojo.dto.BannerUpdateDTO;
import com.sz.applet.miniBusiness.service.BannerService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 小程序Banner表 Controller
 * </p>
 *
 * @author sz
 * @since 2024-09-27
 */
@Tag(name = "小程序Banner")
@RestController
@RequestMapping("wechat/mini/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "创建Banner")
    @PostMapping
    public ApiResult<Void> create(@RequestBody BannerCreateDTO dto) {
        // TODO: 实现创建Banner接口
        return ApiResult.success();
    }

    @Operation(summary = "更新Banner")
    @PutMapping
    public ApiResult<Void> update(@RequestBody BannerUpdateDTO dto) {
        // TODO: 实现更新Banner接口
        return ApiResult.success();
    }

    @Operation(summary = "删除Banner")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        // TODO: 实现删除Banner接口
        return ApiResult.success();
    }

    @Operation(summary = "Banner详情")
    @GetMapping("/{id}")
    public ApiResult<Object> detail(@PathVariable Integer id) {
        // TODO: 实现Banner详情接口
        return ApiResult.success();
    }

    @Operation(summary = "Banner列表")
    @GetMapping
    public ApiResult<PageResult<Object>> page(BannerListDTO dto) {
        // TODO: 实现Banner列表接口
        return ApiResult.success();
    }

    @Operation(summary = "Banner列表(全部)")
    @GetMapping("/list")
    public ApiResult<List<Object>> list(BannerListDTO dto) {
        // TODO: 实现Banner列表接口(全部)
        return ApiResult.success();
    }
}