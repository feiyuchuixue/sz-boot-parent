<#setting number_format="0"><?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

<#if roleMenuList?has_content>
<#list roleMenuList as roleMenu>
    <changeSet id="export-role-menu-${roleMenu.roleId}-${((roleMenu.permissionType)!'')?xml}-${roleMenu.menuId}-${((roleMenu.dataScopeCd)!'empty')?xml}" author="${((author)!'sz')?xml}">
        <preConditions onFail="MARK_RAN">
            <sqlCheck expectedResult="0">SELECT COUNT(*) FROM sys_role_menu WHERE role_id = ${roleMenu.roleId} AND permission_type = '${((roleMenu.permissionType)!'')?xml}' AND menu_id = ${roleMenu.menuId} AND data_scope_cd = '${((roleMenu.dataScopeCd)!'')?xml}'</sqlCheck>
        </preConditions>
        <insert tableName="sys_role_menu">
            <column name="role_id" valueNumeric="${roleMenu.roleId}"/>
            <column name="permission_type" value="${((roleMenu.permissionType)!'')?xml}"/>
            <column name="menu_id" valueNumeric="${roleMenu.menuId}"/>
            <column name="data_scope_cd" value="${((roleMenu.dataScopeCd)!'')?xml}"/>
        </insert>
    </changeSet>
</#list>
</#if>

<#if dataRoleRelationList?has_content>
<#list dataRoleRelationList as relation>
    <changeSet id="export-data-role-relation-${relation.roleId}-${relation.menuId}-${((relation.relationTypeCd)!'')?xml}-${relation.relationId}" author="${((author)!'sz')?xml}">
        <preConditions onFail="MARK_RAN">
            <sqlCheck expectedResult="0">SELECT COUNT(*) FROM sys_data_role_relation WHERE role_id = ${relation.roleId} AND menu_id = ${relation.menuId} AND relation_type_cd = '${((relation.relationTypeCd)!'')?xml}' AND relation_id = ${relation.relationId}</sqlCheck>
        </preConditions>
        <insert tableName="sys_data_role_relation">
            <column name="id" valueNumeric="${relation.id}"/>
            <column name="role_id" valueNumeric="${relation.roleId}"/>
            <column name="relation_type_cd" value="${((relation.relationTypeCd)!'')?xml}"/>
            <column name="relation_id" valueNumeric="${relation.relationId}"/>
            <column name="menu_id" valueNumeric="${relation.menuId}"/>
        </insert>
    </changeSet>
</#list>
</#if>
</databaseChangeLog>
