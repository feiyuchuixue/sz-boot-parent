package ${voPkg};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasExcel == true>
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
  <#if hasDict == true>
import com.sz.excel.annotation.DictFormat;
  </#if>
</#if>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>

/**
 * <p>
 * ${poClassName}返回vo
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}返回vo")
public class ${voClassName} {

<#list columns as field>
<#if field.isList == "1" >
  <#if field.isExport == "1" && hasExcel == true>
    <#if field.dictType != "">
    @ExcelProperty(value = "${field.columnComment}")
      <#if field.dictShowWay == "0">
    @DictFormat(dictType = "${field.dictType}")
      <#else>
    @DictFormat(dictType = "${field.dictType}", useAlias = true)
      </#if>
    <#else>
    @ExcelProperty(value = "${field.columnComment}")
    </#if>
    <#if field.javaType == "LocalDateTime">
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    </#if>
  <#elseif hasExcel == true>
    @ExcelIgnore
  </#if>
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};

</#if>
</#list>
}