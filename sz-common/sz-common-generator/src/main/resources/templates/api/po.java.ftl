package ${poPkg};

import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import com.sz.mysql.EntityChangeListener;
<#list importPackages as pkg>
import ${pkg};
</#list>
/**
* <p>
* ${tableComment}
* </p>
*
* @author ${author}
* @since ${datetime}
*/
@Data
@Table(value = "${tableName}", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "${tableComment}")
public class ${poClassName} implements Serializable {

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