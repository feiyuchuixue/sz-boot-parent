package com.sz.applet.miniBusiness.controller;

import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import com.sz.applet.miniBusiness.service.AdminAuthService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    
    @Autowired
    private AdminAuthService adminAuthService;
    
    /**
     * 获取认证申请列表（分页）
     */
    @GetMapping("/applications")
    public ApiResult getAuthApplications(@RequestParam(defaultValue = "0") Integer status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        PageResult<SchoolUser> result = adminAuthService.getAuthApplications(status, page, size);
        return ApiResult.success(result);
    }
    
    /**
     * 审核认证申请
     */
    @PostMapping("/review/{id}")
    public ApiResult reviewAuthApplication(@PathVariable Long id,
                                         @RequestParam Integer status,
                                         @RequestParam(required = false) String remark,
                                         @RequestAttribute Long adminId) {
        return adminAuthService.reviewAuthApplication(id, status, remark, adminId);
    }
}