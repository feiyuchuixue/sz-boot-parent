package com.sz.admin;

import com.sz.core.util.JsonUtils;
import com.sz.wechat.WechatService;
import com.sz.wechat.pojo.LoginInfoResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName WechatServiceTest
 * @Author sz
 * @Date 2024/4/26 10:41
 * @Version 1.0
 */
@SpringBootTest
public class WechatServiceTest {

    @Autowired
    private WechatService wechatService;

    @Test
    void test() {
        String accessToken = wechatService.getAccessToken();
        System.out.println("accessToken ==" + accessToken);
    }

    @Test
    void testLogin() {
        String code = "";
        String accessToken = wechatService.getAccessToken();
        LoginInfoResult result = wechatService.miniLogin(code, accessToken);
        System.out.println("result ==" + JsonUtils.toJsonString(result));
        System.out.println("response success =" + wechatService.validSuccess(result));
    }

}
