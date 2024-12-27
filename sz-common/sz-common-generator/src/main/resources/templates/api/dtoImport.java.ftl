package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>

import cn.idev.excel.annotation.ExcelProperty;
<#if hasDict == true>
import com.sz.excel.annotation.DictFormat;
</#if>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>
/**
 * <p>
 * ${poClassName}导入DTO
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}导入DTO")
public class ${dtoImportClassName} {

<#list columns as field>
<#if field.isImport == "1" >
  <#-- 时间类型处理-->
  <#if field.javaType == "LocalDateTime">
    @Schema(description =  "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField};
  <#else>
    <#-- 有字典值 -->
    <#if field.dictType != "">
    @ExcelProperty(value = "${field.columnComment}")
      <#if field.dictShowWay == "0">
    @DictFormat(dictType = "${field.dictType}")
      <#else>
    @DictFormat(dictType = "${field.dictType}", useAlias = true)
      </#if>
    <#else>
    <#-- 无字典值-->
    @ExcelProperty(value = "${field.columnComment}")
    </#if>
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
  </#if>

</#if>
</#list>
}