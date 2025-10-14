package com.sz.applet.miniBusiness.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import lombok.Data;

import java.util.Date;

@Data
@Table(value = "school_user_binding", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
public class SchoolUserBinding {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long schoolUserId; // 学校用户ID
    
    private Long miniUserId; // 小程序用户ID
    
    private Integer bindType; // 绑定类型：1-主绑定（认证），2-辅助绑定（共享）
    
    private Integer status; // 绑定状态：0-待审核，1-审核通过，2-审核拒绝
    
    private String delFlag = "F";
    
    private Date createTime;
    
    private Date updateTime;
    
    private Long createId;
    
    private Long updateId;
}