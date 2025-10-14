package com.sz.applet.miniuser.service;

import com.mybatisflex.core.service.IService;
import com.sz.applet.miniuser.pojo.dto.MiniLoginDTO;
import com.sz.applet.miniuser.pojo.po.MiniLoginUser;
import com.sz.applet.miniuser.pojo.po.MiniUser;
import com.sz.applet.miniuser.pojo.vo.MiniUserVO;

/**
 * <p>
 * 小程序用户表 Service
 * </p>
 *
 * @author sz
 * @since 2024-04-26
 */
public interface MiniUserService extends IService<MiniUser> {

    MiniUserVO doLogin(MiniLoginDTO dto);

    MiniLoginUser getUserByOpenId(String openId, String unionid);
    
    /**
     * 检查用户是否已绑定学校用户
     * @param miniUserId 小程序用户ID
     * @return true-已绑定，false-未绑定
     */
    boolean isBoundToSchoolUser(Long miniUserId);
    
    /**
     * 获取用户绑定的学校用户信息
     * @param miniUserId 小程序用户ID
     * @return 学校用户信息
     */
    Object getBoundSchoolUser(Long miniUserId);
}