<#setting number_format="0"><?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

<#if dictTypeList?has_content>
<#list dictTypeList as dictType>
    <changeSet id="export-dict-type-${dictType.id}" author="${((author)!'sz')?xml}">
        <preConditions onFail="MARK_RAN">
            <sqlCheck expectedResult="0">SELECT COUNT(*) FROM sys_dict_type WHERE id = ${dictType.id}</sqlCheck>
        </preConditions>
        <insert tableName="sys_dict_type">
            <column name="id" valueNumeric="${dictType.id}"/>
            <column name="type_name" value="${((dictType.typeName)!'')?xml}"/>
            <column name="type_code" value="${((dictType.typeCode)!'')?xml}"/>
            <column name="source_code" value="${((dictType.sourceCode)!'')?xml}"/>
            <column name="is_lock" value="${((dictType.isLock.code)!'F')?xml}"/>
            <column name="is_show" value="${((dictType.isShow.code)!'T')?xml}"/>
            <column name="del_flag" value="${((dictType.delFlag.code)!'F')?xml}"/>
            <column name="remark" value="${((dictType.remark)!'')?xml}"/>
            <#if dictType.createTime??><column name="create_time" valueDate="${dictType.createTime}"/></#if>
            <#if dictType.updateTime??><column name="update_time" valueDate="${dictType.updateTime}"/></#if>
            <#if dictType.createId??><column name="create_id" valueNumeric="${dictType.createId}"/></#if>
            <#if dictType.updateId??><column name="update_id" valueNumeric="${dictType.updateId}"/></#if>
        </insert>
    </changeSet>
</#list>
</#if>

<#if dictList?has_content>
<#list dictList as dict>
    <changeSet id="export-dict-${dict.id}" author="${((author)!'sz')?xml}">
        <preConditions onFail="MARK_RAN">
            <sqlCheck expectedResult="0">SELECT COUNT(*) FROM sys_dict WHERE id = ${dict.id}</sqlCheck>
        </preConditions>
        <insert tableName="sys_dict">
            <column name="id" valueNumeric="${dict.id}"/>
            <column name="sys_dict_type_id" valueNumeric="${dict.sysDictTypeId}"/>
            <column name="code_name" value="${((dict.codeName)!'')?xml}"/>
            <column name="alias" value="${((dict.alias)!'')?xml}"/>
            <column name="sort" valueNumeric="${(dict.sort)!0}"/>
            <column name="callback_show_style" value="${((dict.callbackShowStyle)!'')?xml}"/>
            <column name="remark" value="${((dict.remark)!'')?xml}"/>
            <column name="is_lock" value="${((dict.isLock.code)!'F')?xml}"/>
            <column name="is_show" value="${((dict.isShow.code)!'T')?xml}"/>
            <column name="del_flag" value="${((dict.delFlag.code)!'F')?xml}"/>
            <#if dict.createTime??><column name="create_time" valueDate="${dict.createTime}"/></#if>
            <#if dict.updateTime??><column name="update_time" valueDate="${dict.updateTime}"/></#if>
            <#if dict.createId??><column name="create_id" valueNumeric="${dict.createId}"/></#if>
            <#if dict.updateId??><column name="update_id" valueNumeric="${dict.updateId}"/></#if>
        </insert>
    </changeSet>
</#list>
</#if>
</databaseChangeLog>
