package com.sz.applet.miniBusiness.controller;

import com.mybatisflex.core.paginate.Page;
import com.sz.applet.miniBusiness.pojo.bo.BannerBo;
import com.sz.applet.miniBusiness.pojo.bo.BannerListBO;
import com.sz.applet.miniBusiness.pojo.vo.BannerVO;
import com.sz.applet.miniBusiness.service.BannerService;
import com.sz.core.common.entity.ApiResult;
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
    public ApiResult<Void> create(@RequestBody BannerBo bo) {
        bannerService.create(bo);
        return ApiResult.success();
    }

    @Operation(summary = "更新Banner")
    @PutMapping
    public ApiResult<Void> update(@RequestBody BannerBo bo) {
        bannerService.update(bo);
        return ApiResult.success();
    }

    @Operation(summary = "删除Banner")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        bannerService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "Banner详情")
    @GetMapping("/{id}")
    public ApiResult<BannerVO> detail(@PathVariable Long id) {
        return ApiResult.success(bannerService.detail(id));
    }

    @Operation(summary = "Banner列表")
    @GetMapping
    public ApiResult<Page<BannerVO>> page(BannerListBO bo) {
        return ApiResult.success(bannerService.page(bo));
    }

    @Operation(summary = "Banner列表(全部)")
    @GetMapping("/list")
    public ApiResult<List<BannerVO>> list(BannerListBO bo) {
        return ApiResult.success(bannerService.list(bo));
    }
}