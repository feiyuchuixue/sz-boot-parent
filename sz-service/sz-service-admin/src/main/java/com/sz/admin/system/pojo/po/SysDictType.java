package com.sz.admin.system.pojo.po;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.core.common.enums.TrueFalseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典类型
 * </p>
 *
 * @author sz
 * @since 2023-08-18
 */
@Data
@Table("sys_dict_type")
@Schema(description = "字典类型")
public class SysDictType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "字典类型id")
    private Long id;

    @Schema(description = "字典类型名(中文)")
    private String typeName;

    @Schema(description = "字典类型码(英文)")
    private String typeCode;

    @Schema(description = "是否锁定，锁定的属性无法在页面进行修改")
    private TrueFalseEnum isLock;

    @Schema(description = "显示与否")
    private TrueFalseEnum isShow;

    @Schema(description = "是否删除")
    @Column(isLogicDelete = true)
    private TrueFalseEnum delFlag;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "字典类型")
    private String type;
}
