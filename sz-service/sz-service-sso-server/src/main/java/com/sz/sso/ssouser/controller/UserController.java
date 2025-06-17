package com.sz.sso.ssouser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.sso.ssouser.service.UserService;
import com.sz.sso.ssouser.pojo.dto.UserCreateDTO;
import com.sz.sso.ssouser.pojo.dto.UserUpdateDTO;
import com.sz.sso.ssouser.pojo.dto.UserListDTO;
import com.sz.sso.ssouser.pojo.vo.UserVO;

/**
 * <p>
 * 平台用户表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-16
 */
@Tag(name =  "平台用户表")
@RestController
@RequestMapping("sso-user")
@RequiredArgsConstructor
public class UserController  {

    private final UserService userService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "sso.user.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody UserCreateDTO dto) {
        userService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "sso.user.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody UserUpdateDTO dto) {
        userService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "sso.user.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        userService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "sso.user.query_table")
    @GetMapping
    public ApiResult<PageResult<UserVO>> list(UserListDTO dto) {
        return ApiPageResult.success(userService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "sso.user.query_table")
    @GetMapping("/{id}")
    public ApiResult<UserVO> detail(@PathVariable Object id) {
        return ApiResult.success(userService.detail(id));
    }
}