package ${controllerPkg};


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
<#if GeneratorInfo.hasImport == "1">
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
</#if>
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.constant.GlobalConstant;

import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import ${servicePkg}.${serviceClassName};
import ${dtoPkg}.${dtoCreateClassName};
import ${dtoPkg}.${dtoUpdateClassName};
import ${dtoPkg}.${dtoListClassName};
import ${voPkg}.${voClassName};
<#if GeneratorInfo.hasImport == "1">
import org.springframework.web.multipart.MultipartFile;
</#if>
<#if GeneratorInfo.hasExport == "1">
import jakarta.servlet.http.HttpServletResponse;
</#if>

/**
 * <p>
 * ${tableComment} Controller
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Tag(name =  "${tableComment}")
@RestController
@RequestMapping("${router}")
@RequiredArgsConstructor
public class ${controllerClassName}  {

<#assign serviceName = lower_case_first_letter(serviceClassName)>
    private final ${serviceClassName} ${serviceName};

    @Operation(summary = "新增")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${createPermission}", orRole = GlobalConstant.SUPER_ROLE)
</#if>
    @PostMapping
    public ApiResult create(@RequestBody ${dtoCreateClassName} dto) {
        ${serviceName}.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${updatePermission}", orRole = GlobalConstant.SUPER_ROLE)
</#if>
    @PutMapping
    public ApiResult update(@RequestBody ${dtoUpdateClassName} dto) {
        ${serviceName}.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${removePermission}", orRole = GlobalConstant.SUPER_ROLE)
</#if>
    @DeleteMapping
    public ApiResult remove(@RequestBody SelectIdsDTO dto) {
        ${serviceName}.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "${listPermission}", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping
    public ApiResult<PageResult<${voClassName}>> list(${dtoListClassName} dto) {
        return ApiPageResult.success(${serviceName}.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "${listPermission}", orRole = GlobalConstant.SUPER_ROLE)
    @GetMapping("/{id}")
    public ApiResult<${voClassName}> detail(@PathVariable Object id) {
        return ApiResult.success(${serviceName}.detail(id));
    }

<#if GeneratorInfo.hasImport == "1">
    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    <#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${importPermission}", orRole = GlobalConstant.SUPER_ROLE)
    </#if>
    @PostMapping("/import")
    public void importExcel(MultipartFile file) {
        ${serviceName}.importExcel(file);
    }
</#if>

<#if GeneratorInfo.hasExport == "1">
    @Operation(summary = "导出")
  <#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${exportPermission}", orRole = GlobalConstant.SUPER_ROLE)
  </#if>
    @PostMapping("/export")
    public void exportExcel(${dtoListClassName} dto, HttpServletResponse response) {
        ${serviceName}.exportExcel(dto, response);
    }
</#if>

}
<#-- 内建函数，实现首字母小写 -->
<#function lower_case_first_letter str>
    <#return str?substring(0, 1)?lower_case + str?substring(1)>
</#function>