package ${poPkg};

<#assign hasLogicDelete = false>
<#assign hasDeleteTimeColumn = false>
<#assign hasDeleteByColumn = false>
<#list columns as field>
    <#if field.isLogicDel == "1">
        <#assign hasLogicDelete = true>
    </#if>
    <#if field.columnName == "delete_time">
        <#assign hasDeleteTimeColumn = true>
    </#if>
    <#if field.columnName == "delete_id">
        <#assign hasDeleteByColumn = true>
    </#if>
</#list>
<#assign hasLogicDeleteFill = hasLogicDelete && (hasDeleteTimeColumn || hasDeleteByColumn)>
<#compress>
import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.io.Serial;
import com.sz.db.EntityChangeListener;
import com.sz.db.id.SzIdGenerator;
<#if hasLogicDeleteFill>
import com.sz.db.LogicDeleteFill;
</#if>
<#list importPackages as pkg>
import ${pkg};
</#list>
</#compress>


/**
 * <p>
 * ${tableComment}
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
<#if hasLogicDeleteFill>
    <#if hasDeleteTimeColumn && hasDeleteByColumn>
@LogicDeleteFill
    <#elseif hasDeleteTimeColumn>
@LogicDeleteFill(deleteByColumn = "")
    <#else>
@LogicDeleteFill(deleteTimeColumn = "")
    </#if>
</#if>
<#if GeneratorInfo.isAutofill == "1">
@Table(value = "${tableName}", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
<#else>
@Table(value = "${tableName}")
</#if>
@Schema(description = "${tableComment}")
public class ${poClassName} implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list columns as field>
    <#-- 主键 -->
    <#if field.isPk == "1">
        <#if field.isIncrement == "1">
    @Id(keyType = KeyType.Auto)
        <#else>
    @Id(keyType = KeyType.Generator, value = SzIdGenerator.NAME)
        </#if>
    </#if>
    <#-- 逻辑删除 -->
    <#if field.isLogicDel == "1">
    @Column(isLogicDelete = true)
    </#if>
    <#if field.javaType?starts_with("List")>
    @Column(typeHandler = Jackson3TypeHandler.class)
    </#if>
    @Schema(description = "${field.columnComment}")
    private ${field.javaType} ${field.javaField};

</#list>
<#-- ----------  END 字段循环遍历  ---------->
}
