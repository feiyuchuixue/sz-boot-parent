package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.core.common.enums.TrueFalseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author sz
 * @since 2023-08-20
 */
@Data
@Table("sys_dict")
@Schema(description ="字典表")
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="字典id(规则)")
    private Long id;

    @Schema(description ="关联sys_dict_type id")
    private Long sysDictTypeId;

    @Schema(description ="字典名称")
    private String codeName;

    @Schema(description = "字典别名")
    private String alias;

    @Schema(description ="排序(正序)")
    private Integer sort;

    @Schema(description ="回显样式")
    private String callbackShowStyle;

    @Schema(description ="备注")
    private String remark;

    @Schema(description ="是否锁定")
    private TrueFalseEnum isLock;

    @Schema(description ="是否展示")
    private TrueFalseEnum isShow;

    @Schema(description ="是否删除")
    @Column(isLogicDelete = true)
    private TrueFalseEnum delFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
