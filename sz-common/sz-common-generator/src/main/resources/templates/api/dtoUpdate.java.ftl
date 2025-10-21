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
    @Schema(description =  "${field.columnComment}")
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
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
    </#if>

  </#if>
</#list>
}