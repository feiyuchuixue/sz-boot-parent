package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 系统角色-菜单表
 * </p>
 *
 * @author sz
 * @since 2023-08-21
 */
@Data
@Table("sys_role_menu")
@Schema(description = "系统角色-菜单表")
public class SysRoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "id")
    private Long id;

    @Schema(description = "sys_menu_id （菜单表）")
    private String menuId;

    @Schema(description = "sys_role_id （角色表）")
    private Long roleId;
}
