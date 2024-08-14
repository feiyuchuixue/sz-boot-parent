package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>
/**
 * <p>
 * ${poClassName}查询DTO
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}查询DTO")
public class ${dtoListClassName} extends PageQuery {

<#list columns as field>
<#if field.isQuery == "1" >
  <#-- 范围查询 -->
  <#if field.queryType == "BETWEEN">
  <#-- 范围查询-日期 -->
    <#if field.javaType == "LocalDateTime">
    @Schema(description =  "${field.columnComment}开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField}Start;

    @Schema(description =  "${field.columnComment}结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField}End;
    <#else>
    @Schema(description =  "${field.columnComment}开始")
    private ${field.javaType} ${field.javaField}Start;

    @Schema(description =  "${field.columnComment}结束")
    private ${field.javaType} ${field.javaField}End;
    </#if>
  <#else>
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
  </#if>

</#if>
</#list>
}