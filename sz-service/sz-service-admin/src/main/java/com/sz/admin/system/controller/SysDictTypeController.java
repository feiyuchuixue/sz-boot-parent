package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeAddDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeListDTO;
import com.sz.admin.system.pojo.dto.sysdict.SysDictTypeUpDTO;
import com.sz.admin.system.pojo.po.SysDictType;
import com.sz.admin.system.pojo.vo.sysdict.DictTypeVO;
import com.sz.admin.system.service.SysDictTypeService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典类型 前端控制器
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/sys-dict-type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @Operation(summary = "字典类型新增")
    @SaCheckPermission(value = "sys.dict.add_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@Valid @RequestBody SysDictTypeAddDTO dto) {
        sysDictTypeService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "字典类型修改")
    @SaCheckPermission(value = "sys.dict.update_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@Valid @RequestBody SysDictTypeUpDTO dto) {
        sysDictTypeService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除、批量删除")
    @SaCheckPermission(value = "sys.dict.delete_type_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult disable(@RequestBody SelectIdsDTO dto) {
        sysDictTypeService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "字段类型详情查询")
    @GetMapping("{id}")
    public ApiResult<SysDictType> detail(@PathVariable Long id) {
        return ApiResult.success(sysDictTypeService.detail(id));
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.dict.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysDictType>> list(SysDictTypeListDTO dto) {
        return ApiPageResult.success(sysDictTypeService.list(dto));
    }

    @Operation(summary = "下拉字典类型查询")
    @GetMapping("selectOptionsType")
    public ApiResult<List<DictTypeVO>> selectOptionType() {
        return ApiResult.success(sysDictTypeService.selectDictTypeOptions());
    }

}
