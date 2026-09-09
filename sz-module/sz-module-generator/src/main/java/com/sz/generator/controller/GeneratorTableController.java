package com.sz.generator.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.util.FileUtils;
import com.sz.generator.config.ConditionalOnGeneratorEnabled;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.dto.ImportTableDTO;
import com.sz.generator.pojo.dto.SelectTablesDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.vo.GenCheckedInfoVO;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import com.sz.generator.pojo.vo.GeneratorPathOptionsVO;
import com.sz.generator.pojo.vo.GeneratorPreviewVO;
import com.sz.generator.service.GeneratorTableService;
import com.sz.logger.audit.OperationAudit;
import com.sz.logger.audit.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * 代码生成业务表 前端控制器
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
@ConditionalOnGeneratorEnabled
@Tag(name = "代码生成")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class GeneratorTableController {

    private final GeneratorTableService generatorTableService;

    @SaCheckPermission(value = "generator.import", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "导入指定表")
    @OperationAudit(operationType = OperationType.IMPORT, bizId = "#dto.tableName")
    @PostMapping("import")
    public ApiResult<Void> importTables(@RequestBody ImportTableDTO dto) {
        generatorTableService.importTable(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.import", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "查询可导入表列表")
    @GetMapping("schema/list")
    public ApiResult<PageResult<GeneratorTable>> schemaList(DbTableQueryDTO dto) {
        return ApiPageResult.success(generatorTableService.selectDbTableNotInImport(dto));
    }

    @SaCheckPermission(value = "generator.list", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "查询已导入表列表")
    @GetMapping("list")
    public ApiResult<PageResult<GeneratorTable>> list(DbTableQueryDTO dto) {
        return ApiPageResult.success(generatorTableService.selectDbTableByImport(dto));
    }

    @Operation(summary = "查询代码生成配置详情")
    @GetMapping("/{tableName}")
    public ApiResult<GeneratorDetailVO> detail(@PathVariable String tableName) {
        return ApiResult.success(generatorTableService.detail(tableName));
    }

    @SaCheckPermission(value = "generator.update", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "修改代码生成配置")
    @PutMapping()
    public ApiResult<Void> updateGenerator(@RequestBody GeneratorDetailVO dto) {
        generatorTableService.updateGeneratorSetting(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.generator", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "代码生成")
    @OperationAudit(operationType = OperationType.OTHER, bizId = "#tableName")
    @PostMapping("generator/{tableName}")
    public ApiResult<List<String>> generator(@PathVariable String tableName) throws IOException {
        return ApiResult.success(generatorTableService.generator(tableName));
    }

    @SaCheckPermission(value = "generator.remove", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "删除已导入表")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectTablesDTO dto) {
        generatorTableService.remove(dto);
        return ApiResult.success();
    }

    @SaCheckPermission(value = "generator.zip", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "下载代码压缩包")
    @OperationAudit(operationType = OperationType.EXPORT, bizId = "#dto.tableNames")
    @PostMapping("zip")
    public void downloadZip(@RequestBody SelectTablesDTO dto, HttpServletResponse response) throws IOException {
        byte[] data = generatorTableService.downloadZip(dto);
        OutputStream outputStream = FileUtils.getOutputStream(response, "sz-admin-" + dto.getTableNames() + ".zip", data.length);
        // 将字节数组写入输出流
        IOUtils.write(data, outputStream);
        response.flushBuffer();
    }

    @SaCheckPermission(value = "generator.preview", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "预览生成代码")
    @GetMapping("preview/{tableName}")
    public ApiResult<List<GeneratorPreviewVO>> preview(@PathVariable String tableName) throws IOException {
        return ApiResult.success(generatorTableService.preview(tableName));
    }

    @SaCheckPermission(value = "generator.update", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "查询代码生成路径候选")
    @GetMapping("path/options")
    public ApiResult<GeneratorPathOptionsVO> pathOptions() {
        return ApiResult.success(generatorTableService.pathOptions());
    }

    @SaCheckPermission(value = "generator.generator", orRole = GlobalConstant.SUPER_ROLE)
    @Operation(summary = "校验代码生成目录")
    @GetMapping("check/{tableName}")
    public ApiResult<GenCheckedInfoVO> check(@PathVariable String tableName) {
        return ApiResult.success(generatorTableService.checkDist(tableName));
    }

}
