package com.sz.admin.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.admin.system.service.SysTempFileService;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileUpdateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileListDTO;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileVO;

/**
 * <p>
 * 模版文件表 Controller
 * </p>
 *
 * @author sz
 * @since 2024-12-05
 */
@Tag(name = "模板文件管理")
@RestController
@RequestMapping("sys-temp-file")
@RequiredArgsConstructor
public class SysTempFileController {

    private final SysTempFileService sysTempFileService;

    @Operation(summary = "新增模板文件")
    @SaCheckPermission(value = "sys.temp.file.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody SysTempFileCreateDTO dto) {
        sysTempFileService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改模板文件")
    @SaCheckPermission(value = "sys.temp.file.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody SysTempFileUpdateDTO dto) {
        sysTempFileService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除模板文件")
    @SaCheckPermission(value = "sys.temp.file.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysTempFileService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "查询模板文件列表")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping
    public ApiResult<PageResult<SysTempFileVO>> list(SysTempFileListDTO dto) {
        return ApiPageResult.success(sysTempFileService.page(dto));
    }

    @Operation(summary = "查询模板文件详情")
    @SaCheckPermission(value = "sys.temp.file.query_table")
    @GetMapping("/{id}")
    public ApiResult<SysTempFileVO> detail(@PathVariable Long id) {
        return ApiResult.success(sysTempFileService.detail(id));
    }
}
