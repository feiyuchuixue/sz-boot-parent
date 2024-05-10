package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigAddDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigQueryDTO;
import com.sz.admin.system.pojo.dto.sysconfig.SysConfigUpDTO;
import com.sz.admin.system.pojo.po.SysConfig;
import com.sz.admin.system.service.SysConfigService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 参数配置表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2023-11-23
 */
@Tag(name =  "参数配置")
@RestController
@RequestMapping("/sys-config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary ="新增")
    @SaCheckPermission(value = "sys.config.add_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@RequestBody SysConfigAddDTO dto) {
        sysConfigService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary ="修改")
    @SaCheckPermission(value = "sys.config.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@RequestBody SysConfigUpDTO dto) {
        sysConfigService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary ="删除")
    @SaCheckPermission(value = "sys.config.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysConfigService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary ="列表查询")
    @SaCheckPermission(value = "sys.config.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysConfig>> list(SysConfigQueryDTO dto) {
        return ApiPageResult.success(sysConfigService.list(dto));
    }

}
