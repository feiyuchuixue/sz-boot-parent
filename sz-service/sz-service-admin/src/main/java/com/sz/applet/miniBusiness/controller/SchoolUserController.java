package com.sz.applet.miniBusiness.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserUpdateBO;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserVO;
import com.sz.applet.miniBusiness.service.SchoolUserService;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 学校师生表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Tag(name = "学校师生管理")
@RestController
@RequestMapping("/school-user")
@RequiredArgsConstructor
public class SchoolUserController {

    private final SchoolUserService schoolUserService;

    @Operation(summary = "申请认证为学校师生")
    @PostMapping("/apply")
    public ApiResult<Void> applyForCertification(@RequestBody SchoolUserCreateBO bo) {
        schoolUserService.create(bo);
        return ApiResult.success();
    }

    @Operation(summary = "更新学校师生信息")
    @SaCheckPermission(value = "school.user.edit_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping
    public ApiResult<Void> update(@RequestBody SchoolUserUpdateBO bo) {
        schoolUserService.update(bo);
        return ApiResult.success();
    }

    @Operation(summary = "删除学校师生")
    @SaCheckPermission(value = "school.user.delete_btn", orRole = GlobalConstant.SUPER_ROLE)
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        schoolUserService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "学校师生详情")
    @SaCheckPermission(value = "school.user.detail_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<SchoolUserVO> detail(@PathVariable Long id) {
        return ApiResult.success(schoolUserService.detail(id));
    }

    @Operation(summary = "学校师生分页列表")
    @SaCheckPermission(value = "school.user.page_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/page")
    public ApiResult<PageResult<SchoolUserVO>> page(SchoolUserListBO bo) {
        Page<SchoolUserVO> page = schoolUserService.page(bo);
        return ApiPageResult.success(page);
    }

    @Operation(summary = "学校师生列表")
    @SaCheckPermission(value = "school.user.list_btn", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/list")
    public ApiResult<List<SchoolUserVO>> list(SchoolUserListBO bo) {
        return ApiResult.success(schoolUserService.list(bo));
    }
    
    @Operation(summary = "审核学校师生认证申请")
    @SaCheckPermission(value = "school.user.review_btn", orRole = GlobalConstant.SUPER_ROLE)
    @PutMapping("/{id}/review")
    public ApiResult<Void> review(@PathVariable Long id, 
                                  @RequestParam Integer status, 
                                  @RequestParam(required = false) String remark) {
        schoolUserService.review(id, status, remark);
        return ApiResult.success();
    }
}