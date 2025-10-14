package com.sz.applet.miniBusiness.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import lombok.Data;

import java.util.Date;

@Data
@Table(value = "school_user", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
public class SchoolUser {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private Integer identity; // 身份（1-校友，2-教师）
    
    private String idCard; // 身份证号
    
    private String studentId; // 学号
    
    private Integer year; // 毕业年份
    
    private String classNo; // 班级编号
    
    private String teacherId; // 教师编号
    
    private String delFlag = "F";
    
    private Date createTime;
    
    private Date updateTime;
    
    private Long createId;
    
    private Long updateId;
    
    private Integer status = 0; // 申请状态：0-待审核，1-审核通过，2-审核拒绝
    
    private String idCardFront; // 身份证正面
    
    private String idCardBack; // 身份证反面
    
    private String studentCard; // 学生证
    
    private String otherProof; // 其他证明材料
    
    private Long reviewerId; // 审核人ID
    
    private Date reviewTime; // 审核时间
    
    private String reviewRemark; // 审核备注
    
    private Long applicantMiniUserId; // 申请人微信用户ID（第一个认证的微信用户）
}