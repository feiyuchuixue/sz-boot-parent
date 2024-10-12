package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.system.pojo.dto.sysclient.SysClientCreateDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientListDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientUpdateDTO;
import com.sz.admin.system.pojo.vo.sysclient.SysClientVO;
import com.sz.admin.system.service.SysClientService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.security.pojo.ClientVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统授权表 Controller
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Tag(name = "系统授权表")
@RestController
@RequestMapping("sys-client")
@RequiredArgsConstructor
public class SysClientController {

    private final SysClientService sysClientService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sys.client.create", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@RequestBody SysClientCreateDTO dto) {
        sysClientService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sys.client.update", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@RequestBody SysClientUpdateDTO dto) {
        sysClientService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sys.client.remove", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysClientService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sys.client.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<SysClientVO>> list(SysClientListDTO dto) {
        return ApiPageResult.success(sysClientService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sys.client.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<ClientVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysClientService.detail(id));
    }

}
