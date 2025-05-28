package com.sz.wechat.work;

import com.sz.core.util.JsonUtils;
import com.sz.redis.RedisUtils;
import com.sz.wechat.WechatProperties;
import com.sz.wechat.pojo.AccessTokenResult;
import com.sz.wechat.pojo.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sz.wechat.WechatApiConstant.WORK_WECHAT_MESSAGE_SEND_URL;
import static com.sz.wechat.WechatApiConstant.WORK_WECHAT_TOKEN_URL;

/**
 * @author sz
 * @since 2024/4/26 10:04
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkWechatService {

    private final WechatProperties wechatProperties;

    private static final String WECHAT_WORK_TOKEN = "wechat:work:token";

    /**
     * 获取 accessToken（企业微信）。
     * <p>
     * 相关配置参见 {@link WechatProperties.WorkProgramProperties}。 <br>
     * 企业微信官方文档：
     * <a href="https://developer.work.weixin.qq.com/document/path/91039">企业微信-获取
     * access_token</a>
     * </p>
     *
     * @return accessToken
     * @see WechatProperties.WorkProgramProperties
     */
    public String getAccessToken() {
        if (RedisUtils.hasKey(WECHAT_WORK_TOKEN)) {
            return (String) RedisUtils.getValue(WECHAT_WORK_TOKEN);
        } else {
            ResponseEntity<AccessTokenResult> entity = RestClient.create().get()
                    .uri(WORK_WECHAT_TOKEN_URL, wechatProperties.getWork().getCorpId(), wechatProperties.getWork().getCorpSecret()).retrieve()
                    .toEntity(AccessTokenResult.class);
            AccessTokenResult result = entity.getBody();
            assert result != null;
            if (validSuccess(result)) {
                int expireTime = result.getExpiresIn() - 1200;
                RedisUtils.getRestTemplate().opsForValue().set(WECHAT_WORK_TOKEN, result.getAccessToken(), expireTime, TimeUnit.SECONDS);
                return result.getAccessToken();
            } else {
                log.error("【企业微信】 获取accessToken失败，错误码：{}，错误信息：{}", result.getErrcode(), result.getErrmsg());
                return "";
            }
        }
    }

    /**
     * 发送企业微信消息。
     * <p>
     * 相关配置参见 {@link WechatProperties.WorkProgramProperties}。
     * <p>
     * <b>准备工作：</b>
     * <ol>
     * <li>创建应用并获取 <code>agentId</code> 和 <code>secret</code></li>
     * <li>在“应用 - 开发者接口”中设置“网页授权及JS-SDK”可信任域名</li>
     * <li>在“应用 - 开发者接口”中设置“企业可信任IP”为企业服务器的IP地址</li>
     * </ol>
     * 详细文档参见 <a href=
     * "https://developer.work.weixin.qq.com/document/path/96458#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF">企业微信-发送应用消息</a>
     * </p>
     *
     * @param dto
     *            消息内容（TextMessageDTO）
     * @return 发送结果（MessageResult）
     */
    public MessageResult sendMessageText(TextMessageDTO dto) {
        List<String> toUserList = dto.getToUserList();
        if (toUserList == null || toUserList.isEmpty()) {
            throw new IllegalArgumentException("接收人不能为空");
        }
        String touser = String.join("|", toUserList);
        TextMessageBody body = TextMessageBody.builder().touser(touser).agentid(wechatProperties.getWork().getAgentId())
                .text(new TextMessageBody.Text(dto.getContent())).msgtype("text").safe(1).build();

        // 微信小程序登录接口返回content-type是text/plain，因此无法直接映射对象。使用String接收，后续再做转换
        ResponseEntity<String> entity = RestClient.create().post().uri(WORK_WECHAT_MESSAGE_SEND_URL, dto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON).body(body).retrieve().toEntity(String.class);
        return JsonUtils.parseObject(entity.getBody(), MessageResult.class);
    }

    /**
     * 校验微信返回结果是否成功
     *
     * @param errorMessage
     *            微信返回结果
     * @return 是否成功
     */
    public boolean validSuccess(ErrorMessage errorMessage) {
        return errorMessage.getErrcode() == null || errorMessage.getErrcode() == 0;
    }

}
