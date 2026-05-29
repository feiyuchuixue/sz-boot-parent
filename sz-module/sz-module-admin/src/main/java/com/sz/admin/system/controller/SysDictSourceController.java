package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictSourceUpdateDTO;
import com.sz.admin.system.pojo.vo.sysdict.DictSourceVO;
import com.sz.admin.system.service.SysDictSourceService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "字典来源")
@RestController
@RequestMapping("/sys-dict-source")
@RequiredArgsConstructor
public class SysDictSourceController {

    private final SysDictSourceService sysDictSourceService;

    @Operation(summary = "字典来源列表")
    @SaCheckPermission(value = "sys.dict.source.query_table")
    @GetMapping
    public ApiResult<PageResult<DictSourceVO>> list(SysDictSourceListDTO dto) {
        return ApiPageResult.success(sysDictSourceService.page(dto));
    }

    @Operation(summary = "新增字典来源")
    @SaCheckPermission(value = "sys.dict.source.add_btn")
    @PostMapping
    public ApiResult<Void> create(@Valid @RequestBody SysDictSourceCreateDTO dto) {
        sysDictSourceService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改字典来源")
    @SaCheckPermission(value = "sys.dict.source.update_btn")
    @PutMapping
    public ApiResult<Void> update(@Valid @RequestBody SysDictSourceUpdateDTO dto) {
        sysDictSourceService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除字典来源")
    @SaCheckPermission(value = "sys.dict.source.delete_btn")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysDictSourceService.remove(dto);
        return ApiResult.success();
    }
}
