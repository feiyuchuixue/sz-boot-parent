package com.sz.admin.system.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.admin.system.pojo.dto.sysuser.*;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysuser.SysUserVO;
import com.sz.admin.system.service.SysDeptService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.*;
import com.sz.core.common.enums.SocketChannelEnum;
import com.sz.core.common.valid.annotation.NotZero;
import com.sz.core.util.JsonUtils;
import com.sz.redis.WebsocketRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/sys-user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    private final WebsocketRedisService websocketRedisService;

    private final SysDeptService sysDeptService;

    @Operation(summary = "用户注册", description = "系统用户注册")
    @SaIgnore
    @PostMapping("register")
    public ApiResult register(@RequestBody RegisterUserDTO dto) {
        sysUserService.register(dto);
        return ApiResult.success();
    }

    @Operation(summary = "根据账户名获取用户信息", hidden = true)
    @SaIgnore
    @GetMapping("/accountName/{accountName}")
    public ApiResult<SysUserVO> getAuthInfoByAccountName(@PathVariable String accountName) {
        SysUserVO user = sysUserService.getSysUserByUsername(accountName);
        return ApiResult.success(user);
    }

    @Deprecated
    @Operation(summary = "临时获取menu", description = "临时获取menu")
    @GetMapping(value = "/menu/list")
    public ApiResult getMenu() {
        String jsonFile = JsonUtils.readJsonFile("sz-service\\sz-service-admin\\src\\main\\resources\\tmp/menu.json");
        return ApiResult.success(jsonFile);
    }

    @Operation(summary = "添加用户")
    @SaCheckPermission(value = "sys.user.create_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult create(@Valid @RequestBody SysUserAddDTO dto) {
        sysUserService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改用户")
    @SaCheckPermission(value = "sys.user.update_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult update(@Valid @RequestBody SysUserAddDTO dto) {
        sysUserService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "批量删除")
    @SaCheckPermission(value = "sys.user.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        sysUserService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "查询分页列表")
    @SaCheckPermission(value = "sys.user.query_table", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiPageResult<PageResult<SysUserVO>> listPage(SysUserListDTO dto) {
        return ApiPageResult.success(sysUserService.list(dto));
    }

    @Operation(summary = "用户详情")
    @SaIgnore
    @GetMapping("{id}")
    public ApiResult<SysUser> detail(@PathVariable Long id) {
        return ApiResult.success(sysUserService.detail(id));
    }

    @Operation(summary = "用户角色信息查询-（穿梭框）")
    @SaCheckPermission(value = "sys.user.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("role")
    public ApiResult findUserRole(@NotZero @RequestParam Integer userId) {
        return ApiPageResult.success(sysUserService.findSysUserRole(userId));
    }

    @Operation(summary = "用户角色信息修改 -（穿梭框）")
    @SaCheckPermission(value = "sys.user.role_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("role")
    public ApiResult changeUserRole(@Valid @RequestBody SysUserRoleDTO dto) {
        sysUserService.changeSysUserRole(dto);
        return ApiPageResult.success();
    }

    @Operation(summary = "登录信息查询")
    @SaIgnore
    @GetMapping("/userinfo")
    public ApiResult<SysUserVO> getUserInfo() {
        return ApiResult.success(sysUserService.getUserInfo());
    }

    @Operation(summary = "（个人）修改密码")
    @PutMapping("/password")
    public ApiResult changePassword(@Valid @RequestBody SysUserPasswordDTO dto) {
        sysUserService.changePassword(dto);
        return ApiResult.success();
    }

    @Operation(summary = "重置账户密码")
    @PutMapping("/reset/password/{userId}")
    public ApiResult resetPassword(@PathVariable Long userId) {
        sysUserService.resetPassword(userId);
        return ApiResult.success();
    }

    @Deprecated
    @Operation(summary = "测试socket踢下线")
    @PostMapping("kick")
    public ApiResult testKickOff() {
        TransferMessage tm = new TransferMessage();
        tm.setToPushAll(true);
        SocketBean sb = new SocketBean();
        sb.setChannel(SocketChannelEnum.KICK_OFF);
        tm.setMessage(sb);
        websocketRedisService.sendServiceToWs(tm);
        return ApiResult.success();
    }

    @Operation(summary = "账户解锁")
    @SaCheckPermission(value = "sys.user.unlock_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("unlock")
    public ApiResult unlock(@RequestBody SelectIdsDTO dto) {
        sysUserService.unlock(dto);
        return ApiResult.success();
    }

    @Operation(summary = "绑定（批量）用户和部门")
    @SaCheckPermission(value = "sys.user.dept_set_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping("/dept/bind")
    public ApiResult bindDept(@RequestBody UserDeptDTO dto) {
        sysUserService.bindUserDept(dto);
        return ApiResult.success();
    }

    @GetMapping("/dept/tree")
    @Operation(summary = "账户管理-部门树形列表")
    public ApiResult<List<DeptTreeVO>> tree() {
        return ApiResult.success(sysDeptService.getDepartmentTreeWithAdditionalNodes());
    }

}
