package ${dtoPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.ExcelIgnore;
import com.sz.excel.annotation.ExcelTemplate;
import com.sz.excel.annotation.ImportColumn;
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
@ExcelTemplate(alias = "${functionName}导入模板.xlsx")
public class ${dtoImportClassName} {

<#list columns as field>
<#if field.isImport == "1">
  <#if field.javaType?starts_with("List")>
    @ExcelIgnore
    @Schema(description = "${field.columnComment}")
    private ${field.javaType} ${field.javaField};

  <#elseif field.javaType == "LocalDateTime">
    @ExcelIgnore
    @Schema(description = "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField};

  <#elseif field.javaType == "LocalDate">
    @ExcelIgnore
    @Schema(description = "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private ${field.javaType} ${field.javaField};

  <#else>
    <#if field.isRequired == "1">
    @ImportColumn(required = true)
    </#if>
    <#if field.dictType != "">
    @ExcelProperty(value = "${field.columnComment}")
      <#if field.dictShowWay == "0">
    @DictFormat(dictType = "${field.dictType}", isSelected = true)
      <#else>
    @DictFormat(dictType = "${field.dictType}", useAlias = true, isSelected = true)
      </#if>
    <#else>
    @ExcelProperty(value = "${field.columnComment}")
    </#if>
    @Schema(description = "${field.columnComment}")
    private ${field.javaType} ${field.javaField};

  </#if>
</#if>
</#list>
}
