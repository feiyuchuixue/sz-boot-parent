package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>
<#-- 判断是否需要引入校验注解 -->
<#assign needNotBlank = false>
<#assign needNotNull = false>
<#assign needSize = false>
<#list columns as field>
  <#if field.isInsert == "1">
    <#if field.isRequired == "1">
      <#if field.javaType == "String">
        <#assign needNotBlank = true>
      <#else>
        <#assign needNotNull = true>
      </#if>
    </#if>
    <#if field.javaType == "String" && field.columnType?contains("varchar")>
      <#assign needSize = true>
    </#if>
  </#if>
</#list>
<#if needNotBlank>
import jakarta.validation.constraints.NotBlank;
</#if>
<#if needNotNull>
import jakarta.validation.constraints.NotNull;
</#if>
<#if needSize>
import jakarta.validation.constraints.Size;
</#if>

/**
 * <p>
 * ${poClassName}添加DTO
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}添加DTO")
public class ${dtoCreateClassName} {

<#list columns as field>
    <#if field.isInsert == "1" >
        <#if field.javaType == "LocalDateTime" >
    @Schema(description = "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField};
        <#else>
<#if field.javaType?starts_with("List")>
    @Column(typeHandler = Jackson3TypeHandler.class)
</#if>
<#-- 必填校验注解 -->
<#if field.isRequired == "1">
  <#if field.javaType == "String">
    @NotBlank(message = "${field.columnComment}不能为空")
  <#else>
    @NotNull(message = "${field.columnComment}不能为空")
  </#if>
</#if>
<#-- varchar 长度校验 -->
<#if field.javaType == "String" && field.columnType?contains("varchar")>
  <#assign varcharLength = field.columnType?replace("varchar(", "")?replace(")", "")?trim>
    @Size(max = ${varcharLength}, message = "${field.columnComment}长度不能超过${varcharLength}个字符")
</#if>
    @Schema(description = "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
        </#if>

    </#if>
</#list>
}
