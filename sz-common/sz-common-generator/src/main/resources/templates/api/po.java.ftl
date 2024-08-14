package ${poPkg};

<#compress>
import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
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
    @Id
        </#if>
    </#if>
    <#-- 逻辑删除 -->
    <#if field.isLogicDel == "1">
    @Column(isLogicDelete = true)
    </#if>
    @Schema(description ="${field.columnComment}")
    private ${field.javaType} ${field.javaField};

</#list>
<#-- ----------  BEGIN 字段循环遍历  ---------->
}