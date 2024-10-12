package com.sz.wechat.pojo;

import lombok.Data;

/**
 * 微信错误信息实体类
 * 
 * @ClassName ErrorMessage
 * @Author sz
 * @Date 2024/4/26 11:11
 * @Version 1.0
 */
@Data
public class ErrorMessage {

    private Integer errcode;

    private String errmsg;

}
