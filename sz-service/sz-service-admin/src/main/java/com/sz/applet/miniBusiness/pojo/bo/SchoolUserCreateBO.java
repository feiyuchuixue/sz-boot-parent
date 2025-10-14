package com.sz.applet.miniBusiness.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学校师生表新增BO
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Data
@Schema(description = "学校师生表新增BO")
public class SchoolUserCreateBO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Integer status = 0;
    
    @Schema(description = "身份证正面")
    private String idCardFront;
    
    @Schema(description = "身份证反面")
    private String idCardBack;
    
    @Schema(description = "学生证")
    private String studentCard;
    
    @Schema(description = "其他证明材料")
    private String otherProof;
    
    @Schema(description = "申请人微信用户ID")
    private Long applicantMiniUserId;
}