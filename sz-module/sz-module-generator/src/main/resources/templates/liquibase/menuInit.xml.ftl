<#setting number_format="0"><?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

<#list sysMenuList as menu>
    <changeSet id="generated-menu-${menu.id}" author="${((author)!'sz')?xml}">
        <preConditions onFail="MARK_RAN">
            <sqlCheck expectedResult="0">SELECT COUNT(*) FROM sys_menu WHERE id = ${menu.id}</sqlCheck>
        </preConditions>
        <insert tableName="sys_menu">
            <column name="id" valueNumeric="${menu.id}"/>
            <column name="pid" valueNumeric="${(menu.pid)!0}"/>
            <column name="path" value="${((menu.path)!'')?xml}"/>
            <column name="name" value="${((menu.name)!'')?xml}"/>
            <column name="title" value="${((menu.title)!'')?xml}"/>
            <column name="icon" value="${((menu.icon)!'')?xml}"/>
            <column name="component" value="${((menu.component)!'')?xml}"/>
            <column name="redirect" value="${((menu.redirect)!'')?xml}"/>
            <column name="sort" valueNumeric="${(menu.sort)!0}"/>
            <column name="deep" valueNumeric="${(menu.deep)!1}"/>
            <column name="menu_type_cd" value="${((menu.menuTypeCd)!'')?xml}"/>
            <column name="permissions" value="${((menu.permissions)!'')?xml}"/>
            <column name="is_hidden" value="${((menu.isHidden)!'F')?xml}"/>
            <column name="has_children" value="${((menu.hasChildren)!'F')?xml}"/>
            <column name="is_link" value="${((menu.isLink)!'F')?xml}"/>
            <column name="is_full" value="${((menu.isFull)!'F')?xml}"/>
            <column name="is_affix" value="${((menu.isAffix)!'F')?xml}"/>
            <column name="is_keep_alive" value="${((menu.isKeepAlive)!'F')?xml}"/>
            <column name="del_flag" value="${((menu.delFlag)!'F')?xml}"/>
            <column name="use_data_scope" value="${((menu.useDataScope)!'F')?xml}"/>
        </insert>
    </changeSet>
</#list>
</databaseChangeLog>
