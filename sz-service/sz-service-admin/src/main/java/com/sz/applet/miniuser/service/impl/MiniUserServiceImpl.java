package com.sz.applet.miniuser.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniuser.mapper.MiniUserMapper;
import com.sz.applet.miniuser.pojo.dto.MiniLoginDTO;
import com.sz.applet.miniuser.pojo.po.MiniLoginUser;
import com.sz.applet.miniuser.pojo.po.MiniUser;
import com.sz.applet.miniuser.pojo.vo.MiniUserVO;
import com.sz.applet.miniuser.service.MiniUserService;
import com.sz.core.util.JsonUtils;
import com.sz.core.util.Utils;
import com.sz.wechat.mini.MiniWechatService;
import com.sz.wechat.mini.LoginInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.sz.applet.miniuser.pojo.po.table.MiniUserTableDef.MINI_USER;

/**
 * <p>
 * 小程序用户表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-04-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniUserServiceImpl extends ServiceImpl<MiniUserMapper, MiniUser> implements MiniUserService {

    private final MiniWechatService miniWechatService;

    @Override
    public MiniUserVO doLogin(MiniLoginDTO dto) {
        String accessToken = miniWechatService.getAccessToken();
        LoginInfoResult loginInfoResult = miniWechatService.miniLogin(dto.getCode(), accessToken);
        log.info(" 小程序登录返回信息：{}", JsonUtils.toJsonString(loginInfoResult));
        // [do something ...] 结合实际业务进行处理
        return null;
    }

    @Override
    public MiniLoginUser getUserByOpenId(String openId, String unionid) {
        QueryWrapper wrapper = QueryWrapper.create().where(MINI_USER.OPENID.eq(openId));
        MiniUser miniUser = getOne(wrapper);
        if (miniUser == null) {
            // [do something ...] 创建新的微信用户信息
        } else {
            // 绑定了sys_user账户
            if (Utils.isNotNull(miniUser.getSysUserId())) {
                // [do something ...]
            } else {
                // 未绑定sys_user账户
                // [do something ...]
            }
        }

        return null;
    }

    @Override
    public boolean isBoundToSchoolUser(Long miniUserId) {
        return false;
    }

    @Override
    public Object getBoundSchoolUser(Long miniUserId) {
        return null;
    }

}