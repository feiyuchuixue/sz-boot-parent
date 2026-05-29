package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>
<#-- 判断是否需要引入校验注解 -->
<#assign needNotBlank = false>
<#assign needNotNull = true>
<#assign needSize = false>
<#list columns as field>
  <#if field.isEdit == "1" && field.isPk == "0">
    <#if field.isRequired == "1" && field.javaType == "String">
      <#assign needNotBlank = true>
    </#if>
    <#if field.javaType == "String" && field.columnType?contains("varchar")>
      <#assign needSize = true>
    </#if>
  </#if>
</#list>
<#if needNotBlank>
import jakarta.validation.constraints.NotBlank;
</#if>
import jakarta.validation.constraints.NotNull;
<#if needSize>
import jakarta.validation.constraints.Size;
</#if>

/**
 * <p>
 * ${poClassName}修改DTO
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}修改DTO")
public class ${dtoUpdateClassName} {

<#list columns as field>
  <#if field.isPk =="1">
    @NotNull(message = "ID不能为空")
    @Schema(description =  "${field.columnComment}", requiredMode = Schema.RequiredMode.REQUIRED)
    private ${field.javaType} ${field.javaField};

  </#if>
  <#if field.isEdit == "1" && field.isPk == "0" >
    <#if field.javaType == "LocalDateTime">
    @Schema(description =  "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField};
    <#else>
    <#if field.javaType?starts_with("List")>
    @Column(typeHandler = JacksonTypeHandler.class)
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
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
    </#if>

  </#if>
</#list>
}
