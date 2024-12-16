package ${serviceImplPkg};

<#compress>
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ${servicePkg}.${serviceClassName};
import ${poPkg}.${poClassName};
import ${mapperPkg}.${mapperClassName};
<#if GeneratorInfo.generateType != "service">
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryChain;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;
import ${dtoPkg}.${dtoCreateClassName};
import ${dtoPkg}.${dtoUpdateClassName};
import ${dtoPkg}.${dtoListClassName};
<#if GeneratorInfo.hasImport == "1">
import ${dtoPkg}.${dtoImportClassName};
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
</#if>
<#if GeneratorInfo.hasExport == "1">
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
</#if>
<#if GeneratorInfo.hasImport == "1" || GeneratorInfo.hasExport == "1">
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
</#if>

import ${voPkg}.${voClassName};
</#if>
</#compress>


/**
 * <p>
 * ${tableComment} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Service
@RequiredArgsConstructor
public class ${serviceImplClassName} extends ServiceImpl<${mapperClassName}, ${poClassName}> implements ${serviceClassName} {
<#if GeneratorInfo.generateType != "service">
    @Override
    public void create(${dtoCreateClassName} dto){
        ${poClassName} ${camelClassName} = BeanCopyUtils.copy(dto, ${poClassName}.class);
<#if hasUniqueValidField == true>
        long count;
<#list columns as field>
    <#if field.isUniqueValid == "1" && field.isInsert == "1" >
        // 唯一性校验
        count = QueryChain.of(${poClassName}.class).eq(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}()).count();
        CommonResponseEnum.EXISTS.message("${field.javaField}已存在").assertTrue(count > 0);
    </#if>
</#list>
</#if>
        save(${camelClassName});
    }

    @Override
    public void update(${dtoUpdateClassName} dto){
        ${poClassName} ${camelClassName} = BeanCopyUtils.copy(dto, ${poClassName}.class);
        QueryWrapper wrapper;
<#list columns as field>
    <#if field.isPk == "1">
        // id有效性校验
        wrapper = QueryWrapper.create()
        <#list pkColumns as pkField>
            .eq(${poClassName}::get${pkField.upCamelField}, dto.get${pkField.upCamelField}())</#list>;
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
    </#if>
</#list>

<#if hasUniqueValidField == true>
        // 唯一性校验
        long count;
<#list columns as field>
    <#if field.isUniqueValid == "1" && field.isEdit == "1">
        count = QueryChain.of(${poClassName}.class).eq(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}())<#list pkColumns as pkField>.ne(${poClassName}::get${pkField.upCamelField}, dto.get${pkField.upCamelField}())</#list>.count();
        CommonResponseEnum.EXISTS.message("${field.javaField}已存在").assertTrue(count > 0);
    </#if>
</#list>
</#if>
        saveOrUpdate(${camelClassName});
    }

    @Override
    public PageResult<${voClassName}> page(${dtoListClassName} dto){
        Page<${voClassName}> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ${voClassName}.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<${voClassName}> list(${dtoListClassName} dto){
        return listAs(buildQueryWrapper(dto), ${voClassName}.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ${voClassName} detail(Object id){
        ${poClassName} ${camelClassName} = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(${camelClassName});
        return BeanCopyUtils.copy(${camelClassName}, ${voClassName}.class);
    }
<#if GeneratorInfo.hasImport == "1">

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<${dtoImportClassName}> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ${dtoImportClassName}.class, true);
        List<${dtoImportClassName}> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }
</#if>
<#if GeneratorInfo.hasExport == "1">

    @SneakyThrows
    @Override
    public void exportExcel(${dtoListClassName} dto, HttpServletResponse response) {
        List<${voClassName}> list = list(dto);
        String fileName = "${functionName}模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "${functionName}", ${voClassName}.class, os);
    }
</#if>

    private static QueryWrapper buildQueryWrapper(${dtoListClassName} dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(${poClassName}.class);
<#list columns as field>
    <#if field.isQuery == "1">
        <#-- 等于-->
        <#if field.queryType == "EQ">
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.eq(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--不等于-->
        <#elseif field.queryType == "NEQ" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.ne(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--大于-->
        <#elseif field.queryType == "GT" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.gt(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--小于-->
        <#elseif field.queryType == "LT" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.lt(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--BETWEEN-->
        <#elseif field.queryType == "BETWEEN" >
        if (Utils.isNotNull(dto.get${field.upCamelField}Start()) && Utils.isNotNull(dto.get${field.upCamelField}End())) {
            wrapper.between(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}Start(), dto.get${field.upCamelField}End());
        }
        <#--大于等于-->
        <#elseif field.queryType == "GTE" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.gte(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--小于等于-->
        <#elseif field.queryType == "LTE" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.lte(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        <#--模糊-->
        <#elseif field.queryType == "LIKE" >
        if (Utils.isNotNull(dto.get${field.upCamelField}())) {
            wrapper.like(${poClassName}::get${field.upCamelField}, dto.get${field.upCamelField}());
        }
        </#if>
    </#if>
</#list>
        return wrapper;
    }
</#if>
}