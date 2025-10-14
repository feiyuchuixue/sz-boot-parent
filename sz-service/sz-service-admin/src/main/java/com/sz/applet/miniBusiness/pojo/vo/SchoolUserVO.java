package com.sz.applet.miniBusiness.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 学校师生表VO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校师生表VO")
public class SchoolUserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "身份（1-校友，2-教师）")
    private Integer identity;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "毕业年份")
    private Integer year;

    @Schema(description = "班级编号")
    private String classNo;

    @Schema(description = "教师编号")
    private String teacherId;

    @Schema(description = "申请状态：0-待审核，1-审核通过，2-审核拒绝")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;
    
    @Schema(description = "身份证正面")
    private String idCardFront;
    
    @Schema(description = "身份证反面")
    private String idCardBack;
    
    @Schema(description = "学生证")
    private String studentCard;
    
    @Schema(description = "其他证明材料")
    private String otherProof;
    
    @Schema(description = "审核人ID")
    private Long reviewerId;
    
    @Schema(description = "审核时间")
    private Date reviewTime;
    
    @Schema(description = "审核备注")
    private String reviewRemark;
    
    @Schema(description = "申请人微信用户ID")
    private Long applicantMiniUserId;
}