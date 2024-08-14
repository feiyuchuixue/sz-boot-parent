package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
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
   @Schema(description =  "${field.columnComment}")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private ${field.javaType} ${field.javaField};
 <#else>
   @Schema(description =  "${field.columnComment}")
   private ${field.javaType} ${field.javaField};
 </#if>

    </#if>
</#list>
}