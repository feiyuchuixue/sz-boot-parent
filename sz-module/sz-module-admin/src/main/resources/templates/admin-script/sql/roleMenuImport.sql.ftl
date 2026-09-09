<#setting number_format="0">
<#compress>
<#if roleMenuList?has_content>
<#list roleMenuList as roleMenu>
<#if scriptDialect == "postgresql">
INSERT INTO sys_role_menu (role_id, permission_type, menu_id, data_scope_cd)
VALUES (${sql.number(roleMenu.roleId)}, ${sql.value((roleMenu.permissionType)!"")}, ${sql.number(roleMenu.menuId)}, ${sql.value((roleMenu.dataScopeCd)!"")})
ON CONFLICT (role_id, permission_type, menu_id, data_scope_cd) DO NOTHING;
<#else>
INSERT IGNORE INTO sys_role_menu (role_id, permission_type, menu_id, data_scope_cd)
VALUES (${sql.number(roleMenu.roleId)}, ${sql.value((roleMenu.permissionType)!"")}, ${sql.number(roleMenu.menuId)}, ${sql.value((roleMenu.dataScopeCd)!"")});
</#if>
</#list>
</#if>
</#compress>

<#compress>
<#if dataRoleRelationList?has_content>
<#list dataRoleRelationList as relation>
INSERT INTO sys_data_role_relation (id, role_id, relation_type_cd, relation_id, menu_id)
SELECT ${sql.number(relation.id)}, ${sql.number(relation.roleId)}, ${sql.value((relation.relationTypeCd)!"")}, ${sql.number(relation.relationId)}, ${sql.number(relation.menuId)}
WHERE NOT EXISTS (
    SELECT 1 FROM sys_data_role_relation
    WHERE role_id = ${sql.number(relation.roleId)}
      AND menu_id = ${sql.number(relation.menuId)}
      AND relation_type_cd = ${sql.value((relation.relationTypeCd)!"")}
      AND relation_id = ${sql.number(relation.relationId)}
);
</#list>
</#if>
</#compress>
