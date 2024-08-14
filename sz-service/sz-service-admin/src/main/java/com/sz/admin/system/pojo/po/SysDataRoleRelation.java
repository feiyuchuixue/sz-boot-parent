package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 系统数据角色-关联表
 * </p>
 *
 * @author sz-admin
 * @since 2024-07-11
 */
@Data
@Table(value = "sys_data_role_relation")
@Schema(description = "系统数据角色-关联表")
public class SysDataRoleRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "sys_data_role_id （数据角色表）")
    private Long roleId;

    @Schema(description = "关联类型，data_scope_relation_type")
    private String relationTypeCd;

    @Schema(description = "关联表id，联动relation_type_cd")
    private Long relationId;

}