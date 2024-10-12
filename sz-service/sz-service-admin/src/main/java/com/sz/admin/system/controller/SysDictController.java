package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysdict.SysDictCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpdateDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.admin.system.service.SysDictService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/sys-dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService sysDictService;

    @Operation(summary = "字典新增")
    @SaCheckPermission(value = "sys.dict.add_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@Valid @RequestBody SysDictCreateDTO dto) {
        sysDictService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "字典修改")
    @SaCheckPermission(value = "sys.dict.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@Valid @RequestBody SysDictUpdateDTO dto) {
        sysDictService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "批量删除")
    @SaCheckPermission(value = "sys.dict.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysDictService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.dict.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDict>> list(@Valid SysDictListDTO dto) {
        return ApiPageResult.success(sysDictService.list(dto));
    }

    @DebounceIgnore
    @Operation(summary = "系统字典查询-全部")
    @GetMapping("dict")
    public ApiResult<Map<String, List<DictVO>>> listDict() {
        return ApiResult.success(sysDictService.dictAll());
    }

    @Operation(summary = "指定类型系统字典查询")
    @GetMapping("dict/{typeCode}")
    public ApiResult<List<DictVO>> getDictDataByType(@PathVariable String typeCode) {
        return ApiResult.success(sysDictService.getDictByType(typeCode));
    }

    @Operation(summary = "导出sql")
    @SaCheckPermission(value = "sys.dict.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("sql/export")
    public ApiResult<String> exportDictSql(@RequestBody SelectIdsDTO dto) {
        return ApiResult.success(sysDictService.exportDictSql(dto));
    }

}
