package com.sz.applet.miniBusiness.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.UserAuthApplicationMapper;
import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import com.sz.applet.miniBusiness.service.AdminAuthService;
import com.sz.applet.miniuser.service.MiniUserService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl extends ServiceImpl<UserAuthApplicationMapper, SchoolUser> implements AdminAuthService {

    private final MiniUserService miniUserService;
    
    @Override
    public PageResult<SchoolUser> getAuthApplications(Integer status, int page, int size) {
        // 获取总数
        /*int total = userAuthApplicationMapper.countByStatus(status);
        
        // 获取列表
        List<UserAuthApplication> applications = userAuthApplicationMapper.selectByStatusWithPagination(status, page, size);
        
        return new PageResult<>(total, applications);*/
        return null;
    }
    
    @Override
    public ApiResult reviewAuthApplication(Long id, Integer status, String remark, Long adminId) {
        /*try {
            // 获取申请信息
            UserAuthApplication application = userAuthApplicationMapper.selectOneById(id);
            if (application == null) {
                return ApiResult.error("申请不存在");
            }
            
            // 更新申请状态
            application.setStatus(status);
            application.setReviewerId(adminId);
            application.setReviewTime(new Date());
            application.setReviewRemark(remark);
            userAuthApplicationMapper.updateByPrimaryKeySelective(application);
            
            // 如果审核通过，更新用户认证状态
            if (status == 1) { // 审核通过
                User user = userMapper.selectOneById(application.getUserId());
                if (user != null) {
                    user.setAuthStatus(2); // 已认证
                    user.setName(application.getName());
                    user.setStudentId(application.getStudentId());
                    user.setYear(application.getYear());
                    user.setClassNo(application.getClassNo());
                    user.setPhone(application.getPhone());
                    user.setIdCard(application.getIdCard());
                    userMapper.updateByPrimaryKeySelective(user);
                }
            } else if (status == 2) { // 审核拒绝
                User user = userMapper.selectOneById(application.getUserId());
                if (user != null) {
                    user.setAuthStatus(3); // 认证失败
                    userMapper.updateByPrimaryKeySelective(user);
                }
            }
            
            return ApiResult.success("审核完成");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("审核失败");
        }*/
        return null;
    }
}