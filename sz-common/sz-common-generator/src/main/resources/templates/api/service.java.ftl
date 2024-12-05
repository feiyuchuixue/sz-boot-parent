package ${servicePkg};

<#compress>
import com.mybatisflex.core.service.IService;
import ${poPkg}.${poClassName};
<#if GeneratorInfo.generateType != "service">
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

import ${dtoPkg}.${dtoCreateClassName};
import ${dtoPkg}.${dtoUpdateClassName};
import ${dtoPkg}.${dtoListClassName};
import ${voPkg}.${voClassName};
<#if GeneratorInfo.hasImport == "1">
import com.sz.core.common.entity.ImportExcelDTO;
</#if>
<#if GeneratorInfo.hasExport == "1">
import jakarta.servlet.http.HttpServletResponse;
</#if>
</#if>
</#compress>


/**
 * <p>
 * ${tableComment} Service
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
public interface ${serviceClassName} extends IService<${poClassName}> {

<#if GeneratorInfo.generateType != "service">
    void create(${dtoCreateClassName} dto);

    void update(${dtoUpdateClassName} dto);

    PageResult<${voClassName}> page(${dtoListClassName} dto);

    List<${voClassName}> list(${dtoListClassName} dto);

    void remove(SelectIdsDTO dto);

    ${voClassName} detail(Object id);
    <#if GeneratorInfo.hasImport == "1">

    void importExcel(ImportExcelDTO dto);
    </#if>
    <#if GeneratorInfo.hasExport == "1">

    void exportExcel(${dtoListClassName} dto, HttpServletResponse response);
    </#if>
</#if>
}