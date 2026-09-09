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
import java.util.List;
import ${dtoPkg}.${dtoCreateClassName};
import ${dtoPkg}.${dtoUpdateClassName};
import ${dtoPkg}.${dtoListClassName};
<#if GeneratorInfo.hasImport == "1">
import ${excelImporterPkg}.${excelImporterClassName};
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.imports.model.ExcelImportResultVO;
</#if>
<#if GeneratorInfo.hasExport == "1">
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
</#if>
<#if GeneratorInfo.hasImport == "1">
import lombok.SneakyThrows;
</#if>
<#if GeneratorInfo.btnDataScopeType == "1">
import com.sz.core.datascope.DataScopeSession;
</#if>
<#if hasResourceRef == true>
import cn.dev33.satoken.stp.StpUtil;
import com.sz.resource.enums.ResourceAccessResponseEnum;
import com.sz.resource.model.ResourceRef;
import com.sz.resource.service.ResourceReferenceService;
import java.util.Objects;
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
<#if GeneratorInfo.hasImport == "1">

    private final ${excelImporterClassName} excelImporter;
</#if>
<#if hasResourceRef == true>

    private final ResourceReferenceService resourceReferenceService;
</#if>

    @Override
    public void create(${dtoCreateClassName} dto) {
        ${poClassName} ${camelClassName} = BeanCopyUtils.copy(dto, ${poClassName}.class);
<#list resourceRefColumns as field>
        ${camelClassName}.set${field.upCamelField}(resourceReferenceService.normalizeForCreate(dto.get${field.upCamelField}(), "${field.options['upload-files.sceneCode']!'system.protected'}", currentUserId(dto.get${field.upCamelField}())));
</#list>
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
    public void update(${dtoUpdateClassName} dto) {
<#if hasResourceRef == true>
        ${poClassName} existing = getById(dto.get${pkUpCamelName}());
        CommonResponseEnum.INVALID_ID.assertNull(existing);
</#if>
        ${poClassName} ${camelClassName} = BeanCopyUtils.copy(dto, ${poClassName}.class);
<#list resourceRefColumns as field>
        ${camelClassName}.set${field.upCamelField}(resourceReferenceService.normalizeForUpdate(existing.get${field.upCamelField}(), dto.get${field.upCamelField}(), "${field.options['upload-files.sceneCode']!'system.protected'}", currentUserId(dto.get${field.upCamelField}())));
</#list>
<#if hasResourceRef != true>
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

</#if>

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
    public PageResult<${voClassName}> page(${dtoListClassName} dto) {
<#if GeneratorInfo.btnDataScopeType == "1">
        try (var ignored = new DataScopeSession(${poClassName}.class)) {
            Page<${voClassName}> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ${voClassName}.class);
            return PageUtils.getPageResult(page);
        }
<#else>
        Page<${voClassName}> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ${voClassName}.class);
        return PageUtils.getPageResult(page);
</#if>
    }

    @Override
    public List<${voClassName}> list(${dtoListClassName} dto) {
<#if GeneratorInfo.btnDataScopeType == "1">
        try (var ignored = new DataScopeSession(${poClassName}.class)) {
            List<${voClassName}> list = listAs(buildQueryWrapper(dto), ${voClassName}.class);
            return list;
        }
<#else>
        List<${voClassName}> list = listAs(buildQueryWrapper(dto), ${voClassName}.class);
        return list;
</#if>
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ${voClassName} detail(${idJavaType} id) {
<#if GeneratorInfo.btnDataScopeType == "1">
        try (var ignored = new DataScopeSession(${poClassName}.class)) {
            return doDetail(id);
        }
<#else>
        return doDetail(id);
</#if>
    }

    private ${voClassName} doDetail(${idJavaType} id) {
        ${poClassName} ${camelClassName} = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(${camelClassName});
        ${voClassName} vo = BeanCopyUtils.copy(${camelClassName}, ${voClassName}.class);
        return vo;
    }
<#if GeneratorInfo.hasImport == "1">

    @SneakyThrows
    @Override
    public ExcelImportResultVO importExcel(ImportExcelDTO dto) {
        return excelImporter.importExcel(dto);
    }
</#if>
<#if GeneratorInfo.hasExport == "1">

    @SneakyThrows
    @Override
    public void exportExcel(${dtoListClassName} dto, HttpServletResponse response) {
        List<${voClassName}> list = list(dto);
        String fileName = "${functionName}模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "${functionName}", ${voClassName}.class, os, true);
    }
</#if>
<#if hasResourceRef == true>

    @Override
    public void validateResourceAccess(${idJavaType} id, Long resourceId) {
        ${poClassName} record = findAccessibleById(id);
        if (record == null) {
            throw ResourceAccessResponseEnum.RESOURCE_NOT_FOUND.newException();
        }
        boolean matched = <#list resourceRefColumns as field>containsResource(record.get${field.upCamelField}(), resourceId)<#if field_has_next> || </#if></#list>;
        ResourceAccessResponseEnum.ACCESS_DENIED.assertFalse(matched);
    }

    private ${poClassName} findAccessibleById(${idJavaType} id) {
<#if GeneratorInfo.btnDataScopeType == "1">
        try (var ignored = new DataScopeSession(${poClassName}.class)) {
            return getById(id);
        }
<#else>
        return getById(id);
</#if>
    }

    private static boolean containsResource(List<ResourceRef> references, Long resourceId) {
        return references != null && references.stream()
                .anyMatch(reference -> reference != null && Objects.equals(reference.getResourceId(), resourceId));
    }

    private static Long currentUserId(List<ResourceRef> references) {
        return references == null || references.isEmpty() ? null : StpUtil.getLoginIdAsLong();
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
