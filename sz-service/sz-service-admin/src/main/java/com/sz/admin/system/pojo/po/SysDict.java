package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.core.common.enums.TrueFalseEnum;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
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
@Table(value = "sys_dict", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "字典表")
public class SysDict implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "字典id(规则)")
    private Long id;

    @Schema(description = "关联sys_dict_type id")
    private Long sysDictTypeId;

    @Schema(description = "字典名称")
    private String codeName;

    @Schema(description = "字典别名")
    private String alias;

    @Schema(description = "排序(正序)")
    private Integer sort;

    @Schema(description = "回显样式")
    private String callbackShowStyle;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否锁定")
    private TrueFalseEnum isLock;

    @Schema(description = "是否展示")
    private TrueFalseEnum isShow;

    @Schema(description = "是否删除")
    @Column(isLogicDelete = true)
    private TrueFalseEnum delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    @Schema(description = "删除人ID")
    private Long deleteId;
}
