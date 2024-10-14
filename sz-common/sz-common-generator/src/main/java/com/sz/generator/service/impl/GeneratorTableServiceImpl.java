package com.sz.generator.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.*;
import com.sz.generator.core.AbstractCodeGenerationTemplate;
import com.sz.generator.core.CodeModelBuilder;
import com.sz.generator.core.builder.sql.MenuSqlCodeBuilder;
import com.sz.generator.core.util.BuildTemplateUtils;
import com.sz.generator.core.util.GeneratorUtils;
import com.sz.generator.mapper.GeneratorTableMapper;
import com.sz.generator.pojo.dto.DbTableQueryDTO;
import com.sz.generator.pojo.dto.ImportTableDTO;
import com.sz.generator.pojo.dto.MenuCreateDTO;
import com.sz.generator.pojo.dto.SelectTablesDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.pojo.result.SysMenuResult;
import com.sz.generator.pojo.result.TableColumResult;
import com.sz.generator.pojo.result.TableResult;
import com.sz.generator.pojo.vo.CodeGenTempResult;
import com.sz.generator.pojo.vo.GenCheckedInfoVO;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import com.sz.generator.pojo.vo.GeneratorPreviewVO;
import com.sz.generator.service.GeneratorTableColumnService;
import com.sz.generator.service.GeneratorTableService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.sz.generator.core.CodeModelBuilder.SEPARATOR;
import static com.sz.generator.pojo.po.table.GeneratorTableColumnTableDef.GENERATOR_TABLE_COLUMN;
import static com.sz.generator.pojo.po.table.GeneratorTableTableDef.GENERATOR_TABLE;

/**
 * <p>
 * 代码生成业务表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
@Service
@RequiredArgsConstructor
public class GeneratorTableServiceImpl extends ServiceImpl<GeneratorTableMapper, GeneratorTable> implements GeneratorTableService {

    private final GeneratorTableColumnService generatorTableColumnService;

    private final FreeMarkerConfigurer configurer;

    private final GeneratorProperties generatorProperties;

    @Override
    /**
     * 导入表
     * 
     * @param dto
     */
    @Transactional
    public void importTable(ImportTableDTO dto) {
        List<String> tableNames = dto.getTableName();
        // 禁止相同table_name的记录出现多条，先执行清除操作，再生成覆盖
        this.mapper.cleanTableColumnByTableName(tableNames);
        this.mapper.cleanTableRecordByTableName(tableNames);
        List<TableResult> tableResults = this.mapper.selectDbTableListByNames(tableNames);
        GeneratorTable generatorTable;
        GeneratorTableColumn generatorTableColumn;
        List<GeneratorTableColumn> tableColumns = new ArrayList<>();

        String pathApi = "";
        String pathWeb = "";
        if (SpringApplicationContextUtils.isLocalEnv()) {
            String moduleName = generatorProperties.getModuleName();
            String serviceName = generatorProperties.getServiceName();
            String projectRootPath = System.getProperty("user.dir");
            // pathApi = projectRootPath + File.separator + "sz-service" + File.separator +
            // "sz-service-admin";
            pathApi = projectRootPath + File.separator + moduleName + File.separator + serviceName;
            pathWeb = generatorProperties.getPath().getWeb();
        }

        for (TableResult table : tableResults) {
            generatorTable = GeneratorUtils.initGeneratorTable(table);
            generatorTable.setPathApi(pathApi);
            generatorTable.setPathWeb(pathWeb);
            save(generatorTable);
            Long tableId = generatorTable.getTableId();
            String tableName = table.getTableName();
            List<TableColumResult> tableColumResults = this.mapper.selectDbTableColumnsByName(tableName);
            int i = 1;
            for (TableColumResult columResult : tableColumResults) {
                generatorTableColumn = GeneratorUtils.initColumnField(columResult, tableId, i);
                tableColumns.add(generatorTableColumn);
                i++;
            }
        }
        generatorTableColumnService.batchInsert(tableColumns);
    }

    @Override
    /**
     * 查询未导入的表
     * 
     * @param dto
     * @return
     */
    public PageResult<GeneratorTable> selectDbTableNotInImport(DbTableQueryDTO dto) {
        PageUtils.toPage(dto);
        List<GeneratorTable> generatorTables = this.mapper.selectDbTableNotInImport(dto);
        return PageUtils.getPageResult(generatorTables);
    }

    @Override
    /**
     * 查询已经导入的表
     * 
     * @param dto
     * @return
     */
    public PageResult<GeneratorTable> selectDbTableByImport(DbTableQueryDTO dto) {
        PageUtils.toPage(dto);
        List<GeneratorTable> generatorTables = this.mapper.selectDbTableByImport(dto);
        return PageUtils.getPageResult(generatorTables);
    }

    @Override
    /**
     * 代码生成配置详情
     * 
     * @param tableId
     * @return
     */
    public GeneratorDetailVO detail(String tableName) {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorTable one = QueryChain.of(GeneratorTable.class).eq(GeneratorTable::getTableName, tableName).one();
        CommonResponseEnum.NOT_EXISTS.message(1002, "table不存在").assertNull(one);
        Long tableId = one.getTableId();
        List<GeneratorTableColumn> tableColumns = generatorTableColumnService.getTableColumnsByTableId(tableId);
        List<GeneratorDetailVO.Column> columns = BeanCopyUtils.copyList(tableColumns, GeneratorDetailVO.Column.class);
        GeneratorDetailVO.BaseInfo baseInfo = BeanCopyUtils.springCopy(one, GeneratorDetailVO.BaseInfo.class);
        GeneratorDetailVO.GeneratorInfo generatorInfo = BeanCopyUtils.springCopy(one, GeneratorDetailVO.GeneratorInfo.class);
        detailVO.setBaseInfo(baseInfo);
        detailVO.setGeneratorInfo(generatorInfo);
        detailVO.setColumns(columns);
        return detailVO;
    }

    @Transactional
    @Override
    /**
     * 更新代码生成配置
     * 
     * @param generatorDetailVO
     */
    public void updateGeneratorSetting(GeneratorDetailVO generatorDetailVO) {
        Long tableId = generatorDetailVO.getBaseInfo().getTableId();
        GeneratorTable one = QueryChain.of(mapper).eq(GeneratorTable::getTableId, tableId).one();
        CommonResponseEnum.INVALID_ID.assertNull(one);

        GeneratorDetailVO.BaseInfo baseInfo = generatorDetailVO.getBaseInfo();
        GeneratorDetailVO.GeneratorInfo generatorInfo = generatorDetailVO.getGeneratorInfo();
        GeneratorTable table = new GeneratorTable();
        BeanCopyUtils.springCopy(baseInfo, table);
        BeanCopyUtils.springCopy(generatorInfo, table);
        // 更新配置信息
        updateById(table);

        List<GeneratorDetailVO.Column> detailColumns = generatorDetailVO.getColumns();
        List<GeneratorTableColumn> columns = BeanCopyUtils.copyList(detailColumns, GeneratorTableColumn.class);
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setSort(i + 1);
            if (("select".equals(columns.get(i).getHtmlType()))) {
                columns.get(i).setSearchType(columns.get(i).getHtmlType());
            }
        }
        // 更新column设置
        generatorTableColumnService.updateBatchTableColumns(columns);
    }

    @Override
    public List<String> generator(String tableName) throws IOException {
        List<String> messages = new ArrayList<>();
        GeneratorDetailVO detailVO = detail(tableName);
        CodeModelBuilder modelBuilder = new CodeModelBuilder();
        String rootPathApi = detailVO.getGeneratorInfo().getPathApi();
        String rootPathWeb = detailVO.getGeneratorInfo().getPathWeb();
        Map<String, Object> model = modelBuilder.builderBaseInfo(detailVO).builderImportPackage(detailVO).builderDynamicsParam(detailVO).builderPojo(detailVO)
                .builderVue(detailVO).getModel();
        List<AbstractCodeGenerationTemplate> apiTemplates = BuildTemplateUtils.getApiTemplates(configurer, rootPathApi, detailVO, model);
        for (AbstractCodeGenerationTemplate apiTemplate : apiTemplates) {
            CodeGenTempResult result = apiTemplate.buildTemplate(true);
            messages.add(result.getOutputMessage());
        }
        List<AbstractCodeGenerationTemplate> webTemplates = BuildTemplateUtils.getWebTemplates(configurer, rootPathWeb, detailVO, model);
        for (AbstractCodeGenerationTemplate webTemplate : webTemplates) {
            CodeGenTempResult result = webTemplate.buildTemplate(true);
            messages.add(result.getOutputMessage());
        }

        if (shouldInitializeMenu(detailVO)) {
            initMenu(detailVO, model, true);
        }
        return messages;
    }

    /**
     * 磁盘校验
     *
     * @param tableName
     * @return
     */
    @Override
    public GenCheckedInfoVO checkDist(String tableName) {
        GeneratorDetailVO detailVO = detail(tableName);
        String pathApi = detailVO.getGeneratorInfo().getPathApi();
        String pathWeb = detailVO.getGeneratorInfo().getPathWeb();
        boolean apiPathExists = FileUtils.isPathExists(pathApi);
        boolean webPathExists = FileUtils.isPathExists(pathWeb);
        GenCheckedInfoVO checkedInfo = new GenCheckedInfoVO();
        // 如果选择模版包含前端，进行前端校验
        if (("all").equals(detailVO.getGeneratorInfo().getGenerateType())) {
            checkedInfo.setCheckedWebPath(webPathExists);
        }
        checkedInfo.setCheckedApiPath(apiPathExists);
        checkedInfo.setPathApi(pathApi);
        checkedInfo.setPathWeb(pathWeb);
        return checkedInfo;
    }

    @Override
    public byte[] downloadZip(SelectTablesDTO dto) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        try {
            List<String> tableNames = dto.getTableNames();
            // 根据id 获取要导入的表detail
            List<GeneratorDetailVO> detailVOS = getDetailsForTables(tableNames);

            for (GeneratorDetailVO detailVO : detailVOS) {
                CodeModelBuilder modelBuilder = new CodeModelBuilder();
                String rootPathApi = detailVO.getGeneratorInfo().getPathApi();
                String rootPathWeb = detailVO.getGeneratorInfo().getPathWeb();
                Map<String, Object> model = modelBuilder.builderBaseInfo(detailVO).builderImportPackage(detailVO).builderDynamicsParam(detailVO)
                        .builderPojo(detailVO).builderVue(detailVO).getModel();

                List<AbstractCodeGenerationTemplate> apiTemplates = BuildTemplateUtils.getApiTemplates(configurer, rootPathApi, detailVO, model);
                for (AbstractCodeGenerationTemplate apiTemplate : apiTemplates) {
                    CodeGenTempResult apiTmpRes = apiTemplate.buildTemplate(false);
                    addFileToZip(zip, apiTmpRes, model);
                }

                List<AbstractCodeGenerationTemplate> webTemplates = BuildTemplateUtils.getWebTemplates(configurer, rootPathWeb, detailVO, model);
                for (AbstractCodeGenerationTemplate webTemplate : webTemplates) {
                    CodeGenTempResult webTmpRes = webTemplate.buildTemplate(false);
                    addFileToZip(zip, webTmpRes, model);
                }
                if (shouldInitializeMenu(detailVO)) {
                    List<MenuCreateDTO> menuCreateDTOS = initMenu(detailVO, model, false);
                    if (!menuCreateDTOS.isEmpty()) {
                        model.put("sysMenuList", menuCreateDTOS);
                        MenuSqlCodeBuilder menuSqlCodeBuilder = new MenuSqlCodeBuilder(configurer, "", detailVO, model);
                        CodeGenTempResult sqlTmpRes = menuSqlCodeBuilder.buildTemplate(false);
                        addFileToZip(zip, sqlTmpRes, model);
                    }
                }
            }
        } finally {
            zip.close();
        }
        return outputStream.toByteArray();
    }

    @Override
    public List<GeneratorPreviewVO> preview(String tableName) throws IOException {
        List<GeneratorPreviewVO> previews = new ArrayList<>();
        GeneratorDetailVO detailVO = detail(tableName);
        String rootPathApi = detailVO.getGeneratorInfo().getPathApi();
        String rootPathWeb = detailVO.getGeneratorInfo().getPathWeb();
        Map<String, Object> model = new CodeModelBuilder().builderBaseInfo(detailVO).builderImportPackage(detailVO).builderDynamicsParam(detailVO)
                .builderPojo(detailVO).builderVue(detailVO).getModel();

        // 处理 API 模板
        handleTemplates(BuildTemplateUtils.getApiTemplates(configurer, rootPathApi, detailVO, model), previews, model);
        // 处理 Web 模板
        handleTemplates(BuildTemplateUtils.getWebTemplates(configurer, rootPathWeb, detailVO, model), previews, model);
        // 处理 Sql模板
        if (shouldInitializeMenu(detailVO)) {
            List<MenuCreateDTO> menuCreateDTOS = initMenu(detailVO, model, false);
            if (!menuCreateDTOS.isEmpty()) {
                model.put("sysMenuList", menuCreateDTOS);
                MenuSqlCodeBuilder menuSqlCodeBuilder = new MenuSqlCodeBuilder(configurer, "", detailVO, model);
                CodeGenTempResult sqlTmpRes = menuSqlCodeBuilder.buildTemplate(false);
                String relativePath = sqlTmpRes.getRelativePath();
                String templateProcess = renderTemplateString(sqlTmpRes, model);
                String fileName = Paths.get(relativePath).getFileName().toString();
                GeneratorPreviewVO previewVO = new GeneratorPreviewVO();
                previewVO.setCode(templateProcess);
                previewVO.setName(fileName);
                previewVO.setLanguage(sqlTmpRes.getLanguage());
                previewVO.setAlias(sqlTmpRes.getAlias());
                previews.add(previewVO);
            }
        }

        return previews;
    }

    @Override
    public Template getMenuSqlTemplate() throws IOException {
        return configurer.getConfiguration().getTemplate(File.separator + "sql" + File.separator + "menuImport.sql.ftl");
    }

    private void handleTemplates(List<AbstractCodeGenerationTemplate> templates, List<GeneratorPreviewVO> previews, Map<String, Object> model)
            throws IOException {
        for (AbstractCodeGenerationTemplate template : templates) {
            CodeGenTempResult tmpRes = template.buildTemplate(false);
            String relativePath = tmpRes.getRelativePath();
            String fileName = Paths.get(relativePath).getFileName().toString();
            String templateProcess = renderTemplateString(tmpRes, model);

            GeneratorPreviewVO previewVO = new GeneratorPreviewVO();
            previewVO.setCode(templateProcess);
            previewVO.setName(fileName);
            previewVO.setLanguage(tmpRes.getLanguage());
            previewVO.setAlias(tmpRes.getAlias());
            previews.add(previewVO);
        }
    }

    private void addFileToZip(ZipOutputStream zip, CodeGenTempResult tempResult, Map<String, Object> model) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(renderTemplate(tempResult, model))) {
            String relativePath = tempResult.getRelativePath();
            ZipEntry zipEntry = new ZipEntry(relativePath);
            zip.putNextEntry(zipEntry);
            IOUtils.copy(inputStream, zip);
            zip.closeEntry();
        }
    }

    private byte[] renderTemplate(CodeGenTempResult tempResult, Map<String, Object> model) throws IOException {
        return renderTemplateString(tempResult, model).getBytes(StandardCharsets.UTF_8);
    }

    private String renderTemplateString(CodeGenTempResult tempResult, Map<String, Object> model) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            tempResult.getTemplate().process(model, writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException("Error rendering template", e);
        }
    }

    /**
     * 菜单的生成
     *
     * @param detailVO
     * @param model
     */
    @Transactional
    public List<MenuCreateDTO> initMenu(GeneratorDetailVO detailVO, Map<String, Object> model, boolean isInsertDB) {
        List<MenuCreateDTO> menus = new ArrayList<>();
        if (("0").equals(detailVO.getGeneratorInfo().getMenuInitType())) {
            return menus;
        }
        String menuId = Utils.generateUUIDs(); // 按钮父级id,菜单id
        int menuDeep;
        String parentMenuId = detailVO.getGeneratorInfo().getParentMenuId();
        SysMenuResult sysMenuResult = this.mapper.selectSysMenuByPid(parentMenuId);
        if (sysMenuResult != null) {
            menuDeep = sysMenuResult.getDeep() + 1;
        } else {
            menuDeep = 1;
        }
        String routerName = model.get("indexDefineOptionsName").toString(); // eg: TeacherStatisticsView
        String path = SEPARATOR + detailVO.getGeneratorInfo().getModuleName() + SEPARATOR + detailVO.getBaseInfo().getCamelClassName(); // eg:
                                                                                                                                        // /test/teacherStatistics
        String component = SEPARATOR + detailVO.getGeneratorInfo().getModuleName() + SEPARATOR + detailVO.getBaseInfo().getCamelClassName() + SEPARATOR
                + "index"; // eg: /teacher/TeacherStatisticsView/index
        int count = this.mapper.selectMenuCount(parentMenuId);
        String listPermission = model.get("listPermission").toString(); // eg: sys.user.query_table
        MenuCreateDTO menuDto = buildMenu(detailVO, menuId, parentMenuId, path, routerName, component, count, menuDeep, listPermission);
        menus.add(menuDto);
        if (isInsertDB) {
            int menuCount = this.mapper.countMenu(routerName, path, component, parentMenuId);
            String message = String.format("菜单已存在: name=%s, path=%s, component=%s, pid=%s", routerName, path, component, parentMenuId);
            CommonResponseEnum.EXISTS.message(1001, message).assertTrue(menuCount > 0);
            this.mapper.insertMenu(menuDto);
        }
        if (("0").equals(detailVO.getGeneratorInfo().getBtnPermissionType())) {
            return menus;
        }
        // 自动创建按钮权限
        String createPermission = model.get("createPermission").toString();
        String updatePermission = model.get("updatePermission").toString();
        String removePermission = model.get("removePermission").toString();
        int btnCount = this.mapper.selectMenuCount(menuId) * 100;
        MenuCreateDTO bthCreateDto = buildBtn(menuId, "新增", createPermission, btnCount + 100, menuDeep);
        MenuCreateDTO btnUpdateDto = buildBtn(menuId, "修改", updatePermission, btnCount + 200, menuDeep);
        MenuCreateDTO btnRemoveDto = buildBtn(menuId, "删除", removePermission, btnCount + 300, menuDeep);
        menus.add(bthCreateDto);
        menus.add(btnUpdateDto);
        menus.add(btnRemoveDto);
        this.mapper.insertMenu(bthCreateDto);
        this.mapper.insertMenu(btnUpdateDto);
        this.mapper.insertMenu(btnRemoveDto);
        if ("1".equals(detailVO.getGeneratorInfo().getHasImport())) {
            String importPermission = model.get("importPermission").toString();
            MenuCreateDTO btnImportDto = buildBtn(menuId, "导入", importPermission, btnCount + 400, menuDeep);
            menus.add(btnImportDto);
            this.mapper.insertMenu(btnImportDto);
        }
        if ("1".equals(detailVO.getGeneratorInfo().getHasExport())) {
            String exportPermission = model.get("exportPermission").toString();
            MenuCreateDTO btnExportDto = buildBtn(menuId, "导出", exportPermission, btnCount + 500, menuDeep);
            menus.add(btnExportDto);
            this.mapper.insertMenu(btnExportDto);
        }
        this.mapper.syncTreeHasChildren();
        return menus;
    }

    private static MenuCreateDTO buildMenu(GeneratorDetailVO detailVO, String btnParentId, String parentMenuId, String path, String routerName,
            String component, int count, int parentDeep, String permission) {
        MenuCreateDTO createDTO = new MenuCreateDTO();
        createDTO.setId(btnParentId);
        if (Utils.isNotNull(parentMenuId)) {
            createDTO.setPid(parentMenuId);
        }
        createDTO.setPath(path);
        createDTO.setName(routerName);
        createDTO.setTitle(detailVO.getGeneratorInfo().getFunctionName()); // eg: 教师统计
        createDTO.setIcon("");
        createDTO.setComponent(component);
        createDTO.setSort(count * 100 + 100);
        createDTO.setDeep(parentDeep);
        createDTO.setMenuTypeCd("1002002"); // 菜单
        createDTO.setPermissions(permission);
        createDTO.setHasChildren("F");
        return createDTO;
    }

    private MenuCreateDTO buildBtn(String btnParentId, String btnName, String createPermission, int sort, int menuDeep) {
        MenuCreateDTO dto = new MenuCreateDTO();
        dto.setId(Utils.generateUUIDs());
        dto.setPid(btnParentId);
        dto.setPath("");
        dto.setName("");
        dto.setTitle(btnName); // eg: 教师统计
        dto.setIcon("");
        dto.setComponent("");
        dto.setMenuTypeCd("1002003"); // 菜单类型：按钮
        dto.setPermissions(createPermission);
        dto.setDeep(menuDeep + 1);
        dto.setHasChildren("F");
        dto.setSort(sort);
        return dto;
    }

    @Override
    @Transactional
    public void remove(SelectTablesDTO dto) {
        UpdateChain.of(GeneratorTableColumn.class).from(GENERATOR_TABLE_COLUMN)
                .where(GENERATOR_TABLE_COLUMN.TABLE_ID.in(
                        QueryWrapper.create().select(GENERATOR_TABLE.TABLE_ID).from(GENERATOR_TABLE).where(GENERATOR_TABLE.TABLE_NAME.in(dto.getTableNames()))))
                .remove();
        QueryWrapper wrapper = QueryWrapper.create().in(GeneratorTable::getTableName, dto.getTableNames());
        remove(wrapper);
    }

    private List<GeneratorDetailVO> getDetailsForTables(List<String> tableNames) {
        List<GeneratorDetailVO> detailVOS = new ArrayList<>();
        for (String tableName : tableNames) {
            GeneratorDetailVO detail = detail(tableName);
            detailVOS.add(detail);
        }
        return detailVOS;
    }

    private boolean shouldInitializeMenu(GeneratorDetailVO detailVO) {
        return ("1").equals(detailVO.getGeneratorInfo().getMenuInitType()) && (("all").equals(detailVO.getGeneratorInfo().getGenerateType()));
    }

    @Override
    public Template getDictSqlTemplate() throws IOException {
        return configurer.getConfiguration().getTemplate(File.separator + "sql" + File.separator + "dictImport.sql.ftl");
    }

}
