package com.sz.applet.miniBusiness.service;


import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;

public interface AdminAuthService {
    
    /**
     * 获取认证申请列表（分页）
     * @param status 状态（0-待审核，1-审核通过，2-审核拒绝）
     * @param page 页码
     * @param size 每页数量
     * @return 认证申请列表
     */
    PageResult<SchoolUser> getAuthApplications(Integer status, int page, int size);
    
    /**
     * 审核认证申请
     * @param id 申请ID
     * @param status 审核状态（1-通过，2-拒绝）
     * @param remark 审核备注
     * @param adminId 审核员ID
     * @return 处理结果
     */
    ApiResult reviewAuthApplication(Long id, Integer status, String remark, Long adminId);
}