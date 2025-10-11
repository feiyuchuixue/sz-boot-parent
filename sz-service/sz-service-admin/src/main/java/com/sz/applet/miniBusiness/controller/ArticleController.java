package com.sz.applet.miniBusiness.controller;

import com.sz.applet.miniBusiness.pojo.dto.ArticleCreateDTO;
import com.sz.applet.miniBusiness.pojo.dto.ArticleListDTO;
import com.sz.applet.miniBusiness.pojo.dto.ArticleUpdateDTO;
import com.sz.applet.miniBusiness.service.ArticleService;
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
 * 小程序文章表 Controller
 * </p>
 *
 * @author sz
 * @since 2025-09-12
 */
@Tag(name = "小程序文章")
@RestController
@RequestMapping("wechat/mini/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    public ApiResult<Void> create(@RequestBody ArticleCreateDTO dto) {
        // TODO: 实现创建文章接口
        return ApiResult.success();
    }

    @Operation(summary = "更新文章")
    @PutMapping
    public ApiResult<Void> update(@RequestBody ArticleUpdateDTO dto) {
        // TODO: 实现更新文章接口
        return ApiResult.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        // TODO: 实现删除文章接口
        return ApiResult.success();
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public ApiResult<Object> detail(@PathVariable Integer id) {
        // TODO: 实现文章详情接口
        return ApiResult.success();
    }

    @Operation(summary = "文章列表")
    @GetMapping
    public ApiResult<PageResult<Object>> page(ArticleListDTO dto) {
        // TODO: 实现文章列表接口
        return ApiResult.success();
    }

    @Operation(summary = "文章列表(全部)")
    @GetMapping("/list")
    public ApiResult<List<Object>> list(ArticleListDTO dto) {
        // TODO: 实现文章列表接口(全部)
        return ApiResult.success();
    }
}