package com.sz.applet.miniBusiness.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingUpdateBO;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserBindingVO;
import com.sz.applet.miniBusiness.service.SchoolUserBindingService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 学校用户绑定小程序用户 前端控制器
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Tag(name = "学校用户绑定管理")
@RestController
@RequestMapping("/school-user-binding")
@RequiredArgsConstructor
public class SchoolUserBindingController {

    private final SchoolUserBindingService schoolUserBindingService;

    @Operation(summary = "申请绑定学校用户")
    //@SaCheckPermission(value = "school.user.binding.add_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PostMapping
    public ApiResult<Void> create(@RequestBody SchoolUserBindingCreateBO bo) {
        schoolUserBindingService.create(bo);
        return ApiResult.success();
    }

    @Operation(summary = "更新绑定申请")
    @SaCheckPermission(value = "school.user.binding.edit_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult<Void> update(@RequestBody SchoolUserBindingUpdateBO bo) {
        schoolUserBindingService.update(bo);
        return ApiResult.success();
    }

    @Operation(summary = "删除绑定申请")
    @SaCheckPermission(value = "school.user.binding.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        schoolUserBindingService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "绑定详情")
    @SaCheckPermission(value = "school.user.binding.detail_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SchoolUserBindingVO> detail(@PathVariable Long id) {
        return ApiResult.success(schoolUserBindingService.detail(id));
    }

    @Operation(summary = "绑定申请分页列表")
    @SaCheckPermission(value = "school.user.binding.page_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/page")
    public ApiResult<Object> page(SchoolUserBindingListBO bo) {
        return ApiPageResult.success(schoolUserBindingService.page(bo));
    }

    @Operation(summary = "绑定申请列表")
    @SaCheckPermission(value = "school.user.binding.list_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/list")
    public ApiResult<List<SchoolUserBindingVO>> list(SchoolUserBindingListBO bo) {
        return ApiResult.success(schoolUserBindingService.list(bo));
    }

    @Operation(summary = "审核绑定申请")
    @SaCheckPermission(value = "school.user.binding.review_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/{id}/review")
    public ApiResult<Void> review(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        schoolUserBindingService.review(id, status, remark);
        return ApiResult.success();
    }
}