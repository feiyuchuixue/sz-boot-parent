package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 系统用户-角色关联表
 * </p>
 *
 * @author sz
 * @since 2023-08-29
 */
@Getter
@Setter
@Table("sys_user_role")
@Schema(description = "系统用户-角色关联表")
public class SysUserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "角色id (sys_role_id)")
    private Long roleId;

    @Schema(description = " 用户id(sys_user_id)")
    private Long userId;
}
