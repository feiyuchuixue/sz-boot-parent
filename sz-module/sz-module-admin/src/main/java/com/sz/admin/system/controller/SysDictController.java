package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.scriptexport.ScriptExportDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictCreateDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictUpdateDTO;
import com.sz.admin.system.pojo.po.SysDict;
import com.sz.admin.system.pojo.vo.scriptexport.ScriptExportVO;
import com.sz.admin.system.service.SysDictService;
import com.sz.core.common.annotation.DebounceIgnore;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.*;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationType;
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

    @Operation(summary = "新增字典数据")
    @SaCheckPermission(value = "sys.dict.add_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult<Void> create(@Valid @RequestBody SysDictCreateDTO dto) {
        sysDictService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改字典数据")
    @SaCheckPermission(value = "sys.dict.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult<Void> update(@Valid @RequestBody SysDictUpdateDTO dto) {
        sysDictService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除字典数据")
    @SaCheckPermission(value = "sys.dict.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        sysDictService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "查询字典数据列表")
    @SaCheckPermission(value = "sys.dict.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDict>> list(@Valid SysDictListDTO dto) {
        return ApiPageResult.success(sysDictService.list(dto));
    }

    @DebounceIgnore
    @Operation(summary = "查询全部动态字典")
    @GetMapping("dict")
    public ApiResult<Map<String, List<DictVO>>> listDict() {
        return ApiResult.success(sysDictService.dictAll());
    }

    @DebounceIgnore
    @Operation(summary = "查询全部静态字典")
    @GetMapping("static")
    public ApiResult<Map<String, List<DictVO>>> listStaticDict() {
        return ApiResult.success(sysDictService.dictStatic());
    }

    @Operation(summary = "查询指定类型字典")
    @GetMapping("dict/{typeCode}")
    public ApiResult<List<DictVO>> getDictDataByType(@PathVariable String typeCode) {
        return ApiResult.success(sysDictService.getDictByType(typeCode));
    }

    @Operation(summary = "导出字典 Liquibase脚本")
    @OperationAudit(operationType = OperationType.EXPORT)
    @SaCheckPermission(value = "sys.dict.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("sql/export")
    public ApiResult<String> exportDictSql(@RequestBody SelectIdsDTO dto) {
        return ApiResult.success(sysDictService.exportDictSql(dto));
    }

    @Operation(summary = "导出字典 SQL脚本")
    @OperationAudit(operationType = OperationType.EXPORT)
    @SaCheckPermission(value = "sys.dict.sql_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("script/export")
    public ApiResult<ScriptExportVO> exportDictScript(@RequestBody ScriptExportDTO dto) {
        return ApiResult.success(sysDictService.exportDictScript(dto));
    }

    @DebounceIgnore
    @Operation(summary = "按类型批量查询字典")
    @GetMapping("code")
    public ApiResult<Map<String, List<DictVO>>> listDictByCode(@RequestParam("typeCode") List<String> typeCodes) {
        return ApiResult.success(sysDictService.getDictByCode(typeCodes));
    }

}
