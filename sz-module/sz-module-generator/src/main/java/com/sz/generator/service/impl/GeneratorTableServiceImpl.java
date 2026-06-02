package com.sz.generator.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.*;
import com.sz.db.id.SzIdUtil;
import com.sz.generator.config.ConditionalOnGeneratorEnabled;
import com.sz.generator.core.AbstractCodeGenerationTemplate;
import com.sz.generator.core.CodeModelBuilder;
import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.core.metadata.GeneratorDbMetadataService;
import com.sz.generator.core.module.GeneratorBackendModuleScanner;
import com.sz.generator.core.script.ScriptExportService;
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
import com.sz.generator.pojo.vo.GeneratorBackendModuleOptionVO;
import com.sz.generator.pojo.vo.CodeGenTempResult;
import com.sz.generator.pojo.vo.GenCheckedInfoVO;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import com.sz.generator.pojo.vo.GeneratorPathOptionsVO;
import com.sz.generator.pojo.vo.GeneratorPreviewVO;
import com.sz.generator.pojo.vo.ScriptExportItemVO;
import com.sz.generator.pojo.vo.ScriptExportVO;
import com.sz.generator.service.GeneratorTableColumnService;
import com.sz.generator.service.GeneratorTableService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@ConditionalOnGeneratorEnabled
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorTableServiceImpl extends ServiceImpl<GeneratorTableMapper, GeneratorTable> implements GeneratorTableService {

    private static final String OP_CREATE_FILE = "CREATE_FILE";

    private static final String OP_MODIFY_FILE = "MODIFY_FILE";

    private static final String OP_SKIP_EXISTS = "SKIP_EXISTS";

    private static final String OP_SCRIPT = "SCRIPT";

    private static final String DEFAULT_CHANGELOG_VERSION = "unreleased";

    private static final Pattern JAVA_PACKAGE_PATTERN = Pattern.compile("(?m)^\\s*package\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*;");

    private static final Pattern SPRING_BOOT_SCAN_BASE_PACKAGES_PATTERN = Pattern
            .compile("@SpringBootApplication\\s*\\([^)]*scanBasePackages\\s*=\\s*(\\{[^}]*}|\"[^\"]+\")", Pattern.DOTALL);

    private static final Pattern COMPONENT_SCAN_BASE_PACKAGES_PATTERN = Pattern
            .compile("@ComponentScan\\s*\\([^)]*(?:basePackages|value)\\s*=\\s*(\\{[^}]*}|\"[^\"]+\")", Pattern.DOTALL);

    private static final Pattern COMPONENT_SCAN_VALUE_PATTERN = Pattern.compile("@ComponentScan\\s*\\(\\s*(\\{[^}]*}|\"[^\"]+\")");

    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"]+)\"");

    private final GeneratorTableColumnService generatorTableColumnService;

    private final FreeMarkerConfigurer configurer;

    private final GeneratorProperties generatorProperties;

    private final GeneratorDbMetadataService metadataService;

    private final ScriptExportService scriptExportService;

    private final GeneratorBackendModuleScanner backendModuleScanner;

    private final Environment environment;

    /**
     * 导入表
     * 
     * @param dto
     *            导入表名
     */
    @Transactional
    @Override
    public void importTable(ImportTableDTO dto) {
        List<String> tableNames = dto.getTableName();
        // 禁止相同table_name的记录出现多条，先执行清除操作，再生成覆盖
        this.mapper.cleanTableColumnByTableName(tableNames);
        this.mapper.cleanTableRecordByTableName(tableNames);
        List<TableResult> tableResults = metadataService.selectDbTableListByNames(tableNames);
        GeneratorTable generatorTable;
        GeneratorTableColumn generatorTableColumn;
        List<GeneratorTableColumn> tableColumns = new ArrayList<>();

        String pathApi = "";
        String pathWeb = "";
        if (SpringApplicationContextUtils.getInstance().isLocalEnv()) {
            String moduleName = generatorProperties.getModuleName();
            String serviceName = generatorProperties.getServiceName();
            Path projectRoot = resolveProjectRoot();
            String configuredApiPath = generatorProperties.getPath() == null ? null : generatorProperties.getPath().getApi();
            String configuredWebPath = generatorProperties.getPath() == null ? null : generatorProperties.getPath().getWeb();
            pathApi = configuredApiPath == null || configuredApiPath.isBlank() ? resolveDefaultBackendModulePath(projectRoot, moduleName, serviceName)
                    : configuredApiPath;
            pathWeb = configuredWebPath == null ? "" : configuredWebPath;
        }

        boolean enableIgnoreTablePrefix = generatorProperties.getGlobal().getIgnoreTablePrefix().getEnabled();
        String[] prefixes = generatorProperties.getGlobal().getIgnoreTablePrefix().getPrefixes();

        for (TableResult table : tableResults) {
            generatorTable = GeneratorUtils.initGeneratorTable(table, enableIgnoreTablePrefix, prefixes);
            generatorTable.setPathApi(pathApi);
            generatorTable.setPathWeb(pathWeb);
            applyDefaultModuleInfo(generatorTable);
            save(generatorTable);
            Long tableId = generatorTable.getTableId();
            String tableName = table.getTableName();
            List<TableColumResult> tableColumResults = metadataService.selectDbTableColumnsByName(tableName);
            int i = 1;
            for (TableColumResult columResult : tableColumResults) {
                generatorTableColumn = GeneratorUtils.initColumnField(columResult, tableId, i);
                tableColumns.add(generatorTableColumn);
                i++;
            }
        }
        generatorTableColumnService.batchInsert(tableColumns);
    }

    /**
     * 查询未导入的表
     * 
     * @param dto
     *            查询条件
     * @return 未导入的表
     */
    @Override
    public PageResult<GeneratorTable> selectDbTableNotInImport(DbTableQueryDTO dto) {
        return metadataService.selectDbTableNotInImport(dto);
    }

    /**
     * 查询已经导入的表
     * 
     * @param dto
     *            查询条件
     * @return 已经导入的表
     */
    @Override
    public PageResult<GeneratorTable> selectDbTableByImport(DbTableQueryDTO dto) {
        PageUtils.toPage(dto);
        List<GeneratorTable> generatorTables = this.mapper.selectDbTableByImport(dto);
        return PageUtils.getPageResult(generatorTables);
    }

    /**
     * 代码生成配置详情
     * 
     * @param tableName
     *            表名
     * @return 代码生成配置详情
     */
    @Override
    public GeneratorDetailVO detail(String tableName) {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorTable one = QueryChain.of(GeneratorTable.class).eq(GeneratorTable::getTableName, tableName).one();
        CommonResponseEnum.NOT_EXISTS.message(1002, "table不存在").assertNull(one);
        Long tableId = one.getTableId();
        List<GeneratorTableColumn> tableColumns = generatorTableColumnService.getTableColumnsByTableId(tableId);
        List<GeneratorDetailVO.Column> columns = BeanCopyUtils.copyList(tableColumns, GeneratorDetailVO.Column.class);
        GeneratorDetailVO.BaseInfo baseInfo = BeanCopyUtils.copy(one, GeneratorDetailVO.BaseInfo.class);
        GeneratorDetailVO.GeneratorInfo generatorInfo = BeanCopyUtils.copy(one, GeneratorDetailVO.GeneratorInfo.class);
        detailVO.setBaseInfo(baseInfo);
        detailVO.setGeneratorInfo(generatorInfo);
        detailVO.setColumns(columns);
        Set<String> dictTypes = new HashSet<>();
        for (GeneratorDetailVO.Column column : columns) {
            normalizeColumnByHtmlType(column);
            if (column.getDictType() != null && !column.getDictType().isEmpty()) {
                dictTypes.add(column.getDictType());
            }
        }
        detailVO.setDictTypes(dictTypes);
        return detailVO;
    }

    /**
     * 更新代码生成配置
     * 
     * @param generatorDetailVO
     *            代码生成配置
     */
    @Transactional
    @Override
    public void updateGeneratorSetting(GeneratorDetailVO generatorDetailVO) {
        Long tableId = generatorDetailVO.getBaseInfo().getTableId();
        GeneratorTable one = QueryChain.of(mapper).eq(GeneratorTable::getTableId, tableId).one();
        CommonResponseEnum.INVALID_ID.assertNull(one);

        GeneratorDetailVO.BaseInfo baseInfo = generatorDetailVO.getBaseInfo();
        GeneratorDetailVO.GeneratorInfo generatorInfo = generatorDetailVO.getGeneratorInfo();
        GeneratorTable table = new GeneratorTable();
        BeanCopyUtils.copy(baseInfo, table);
        BeanCopyUtils.copy(generatorInfo, table);
        // 更新配置信息
        updateById(table);

        List<GeneratorDetailVO.Column> detailColumns = generatorDetailVO.getColumns();
        List<GeneratorTableColumn> columns = BeanCopyUtils.copyList(detailColumns, GeneratorTableColumn.class);
        for (int i = 0; i < columns.size(); i++) {
            GeneratorTableColumn column = columns.get(i);
            column.setSort(i + 1);
            normalizeColumnByHtmlType(column);
            validateDictionaryDisplayColumn(column);
            if (("select".equals(column.getHtmlType()))) {
                column.setSearchType(column.getHtmlType());
            }
        }
        // 更新column设置
        generatorTableColumnService.updateBatchTableColumns(columns);
    }

    private static void normalizeColumnByHtmlType(GeneratorTableColumn column) {
        String htmlType = column.getHtmlType();
        if ("radio-group".equals(htmlType)) {
            column.setHtmlType(GeneratorConstants.HTML_RADIO);
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_RADIO.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_CHECKBOX.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_FILE_UPLOAD.equals(htmlType) || GeneratorConstants.HTML_IMAGE_UPLOAD.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
            column.setJavaTypePackage("com.sz.db.handler.Jackson3TypeHandler,com.sz.resource.model.ResourceRef,java.util.List,com.mybatisflex.annotation.Column");
            column.setSearchType("input");
            return;
        }
        if (GeneratorConstants.HTML_DATE.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
            column.setJavaTypePackage("java.time.LocalDate");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_TIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
            column.setJavaTypePackage("java.time.LocalTime");
            column.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_DATETIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
            column.setJavaTypePackage("java.time.LocalDateTime");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
        }
    }

    private static void normalizeColumnByHtmlType(GeneratorDetailVO.Column column) {
        String htmlType = column.getHtmlType();
        if ("radio-group".equals(htmlType)) {
            column.setHtmlType(GeneratorConstants.HTML_RADIO);
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_RADIO.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_CHECKBOX.equals(htmlType)) {
            column.setSearchType("select");
            defaultDictShowWay(column);
            return;
        }
        if (GeneratorConstants.HTML_FILE_UPLOAD.equals(htmlType) || GeneratorConstants.HTML_IMAGE_UPLOAD.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
            column.setJavaTypePackage("com.sz.db.handler.Jackson3TypeHandler,com.sz.resource.model.ResourceRef,java.util.List,com.mybatisflex.annotation.Column");
            column.setSearchType("input");
            return;
        }
        if (GeneratorConstants.HTML_DATE.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATE);
            column.setJavaTypePackage("java.time.LocalDate");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_TIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCALTIME);
            column.setJavaTypePackage("java.time.LocalTime");
            column.setSearchType(GeneratorConstants.HTML_TIME_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
            return;
        }
        if (GeneratorConstants.HTML_DATETIME.equals(htmlType)) {
            column.setJavaType(GeneratorConstants.TYPE_LOCAL_DATETIME);
            column.setJavaTypePackage("java.time.LocalDateTime");
            column.setSearchType(GeneratorConstants.HTML_DATE_PICKER);
            column.setQueryType(GeneratorConstants.QUERY_BETWEEN);
        }
    }

    private static void validateDictionaryDisplayColumn(GeneratorTableColumn column) {
        if (!isDictionaryDisplayHtmlType(column.getHtmlType())) {
            return;
        }
        String dictType = column.getDictType();
        CommonResponseEnum.INVALID.message(String.format("字段「%s」选择了 %s，必须配置字典类型", column.getColumnName(), column.getHtmlType()))
                .assertTrue(dictType == null || dictType.isBlank());
    }

    private static boolean isDictionaryDisplayHtmlType(String htmlType) {
        return GeneratorConstants.HTML_SELECT.equals(htmlType) || GeneratorConstants.HTML_RADIO.equals(htmlType)
                || GeneratorConstants.HTML_CHECKBOX.equals(htmlType) || "radio-group".equals(htmlType);
    }

    private static void defaultDictShowWay(GeneratorTableColumn column) {
        if (column.getDictType() != null && !column.getDictType().isBlank()
                && (column.getDictShowWay() == null || column.getDictShowWay().isBlank())) {
            column.setDictShowWay("0");
        }
    }

    private static void defaultDictShowWay(GeneratorDetailVO.Column column) {
        if (column.getDictType() != null && !column.getDictType().isBlank()
                && (column.getDictShowWay() == null || column.getDictShowWay().isBlank())) {
            column.setDictShowWay("0");
        }
    }

    @Override
    public List<String> generator(String tableName) throws IOException {
        List<String> messages = new ArrayList<>();
        GeneratorDetailVO detailVO = detail(tableName);
        CodeModelBuilder modelBuilder = new CodeModelBuilder();
        prepareBackendModuleIfNecessary(detailVO.getGeneratorInfo(), messages);
        prepareBusinessTableChangelogIfNecessary(detailVO, messages);
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
        prepareFrontendComponentRegistrationIfNecessary(detailVO.getGeneratorInfo(), model, messages);

        if (shouldInitializeMenu(detailVO)) {
            initMenu(detailVO, model, true); // 生成菜单
        }
        return messages;
    }

    private void prepareBackendModuleIfNecessary(GeneratorDetailVO.GeneratorInfo info, List<String> messages) throws IOException {
        Path projectRoot = resolveProjectRoot();
        boolean newTarget = "new".equals(info.getBackendTargetType());
        Optional<GeneratorBackendModuleOptionVO> existingOption = backendModuleScanner.findByModuleName(projectRoot, info.getBackendModuleName());
        if (!newTarget && (existingOption.isEmpty() || GeneratorBackendModuleScanner.STATUS_READY.equals(existingOption.get().getStatus()))) {
            return;
        }
        String moduleName = newTarget ? normalizeModuleName(info.getBackendModuleName()) : existingOption.get().getModuleName();
        String moduleCode = newTarget ? normalizeModuleCode(moduleName) : existingOption.get().getModuleCode();
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getApiPrefixModule());
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getFrontendModuleName());
        GeneratorBackendModuleOptionVO option = newTarget ? backendModuleScanner.buildNewModuleOption(projectRoot, moduleName, moduleCode, info.getPathApi())
                : existingOption.get();
        String packageName = info.getPackageName() == null || info.getPackageName().isBlank() ? option.getPackageName() : info.getPackageName();
        String apiPrefix = info.getApiPrefix() == null || info.getApiPrefix().isBlank() ? option.getApiPrefix() : info.getApiPrefix();
        Path modulePath = Paths.get(option.getPath());
        Path serviceRoot = resolveServiceRoot(projectRoot);
        if (newTarget) {
            Optional<String> conflict = findNewBackendModuleConflict(projectRoot, option.getModuleName(), moduleCode, modulePath);
            CommonResponseEnum.EXISTS.message(conflict.orElse("新建模块已存在，请切换为现有模块或更换模块名")).assertTrue(conflict.isPresent());
        }
        Files.createDirectories(modulePath);

        writeIfMissing(modulePath.resolve("pom.xml"), buildModulePom(option.getModuleName()));
        writeIfMissing(modulePath.resolve(javaPath(packageName, "config", upperCamel(moduleCode) + "ApiPrefixConfiguration.java")),
                buildApiPrefixConfiguration(packageName, upperCamel(moduleCode), moduleCode, apiPrefix));
        writeIfMissing(modulePath.resolve(javaPath(packageName, "config", upperCamel(moduleCode) + "MapperScanConfiguration.java")),
                buildMapperScanConfiguration(packageName, upperCamel(moduleCode)));
        writeIfMissing(modulePath.resolve(javaPath(packageName, "config", upperCamel(moduleCode) + "ExcelTemplateScanConfiguration.java")),
                buildExcelTemplateScanConfiguration(packageName, upperCamel(moduleCode)));
        writeIfMissing(modulePath.resolve("src/main/resources/db/changelog/module-" + moduleCode + "-changelog.xml"),
                buildModuleChangelog(moduleCode));
        writeIfMissing(modulePath.resolve("src/main/resources/db/changelog/" + moduleCode + "/changelog-master.xml"),
                buildSubChangelogMaster(List.of()));

        insertIfMissing(projectRoot.resolve(generatorProperties.getModuleName()).resolve("pom.xml"), "<module>" + option.getModuleName() + "</module>",
                "        <module>" + option.getModuleName() + "</module>\n", "    </modules>");
        insertDependencyManagementIfMissing(projectRoot.resolve("pom.xml"), option.getModuleName());
        insertIfMissing(serviceRoot.resolve("pom.xml"),
                "<artifactId>" + option.getModuleName() + "</artifactId>",
                """
                        <dependency>
                            <groupId>com.sz</groupId>
                            <artifactId>%s</artifactId>
                        </dependency>
                """.formatted(option.getModuleName()), "    </dependencies>");
        insertIfMissing(serviceRoot.resolve("src/main/resources/db/changelog/changelog-master.xml"), "module-" + moduleCode + "-changelog.xml",
                "    <include file=\"db/changelog/module-" + moduleCode + "-changelog.xml\" errorIfMissing=\"false\"/>\n", "</databaseChangeLog>");
        insertApiPrefixIfMissing(serviceRoot.resolve("src/main/resources/application.yml"), moduleCode, apiPrefix);

        info.setBackendModuleName(option.getModuleName());
        info.setApiPrefixModule(moduleCode);
        info.setApiPrefix(apiPrefix);
        info.setPathApi(modulePath.normalize().toString());
        if (newTarget && (info.getFrontendModuleName() == null || info.getFrontendModuleName().isBlank())) {
            info.setFrontendModuleName(moduleCode);
        }
        messages.add("Prepared backend module: " + modulePath.normalize());
    }

    private void prepareBusinessTableChangelogIfNecessary(GeneratorDetailVO detailVO, List<String> messages) throws IOException {
        BackendModuleTarget target = resolveBackendModuleTarget(detailVO.getGeneratorInfo());
        if (target.moduleCode().isBlank() || target.modulePath().toString().isBlank()) {
            return;
        }
        Path masterChangelog = subChangelogMasterPath(target);
        Path tableChangelog = businessTableChangelogPath(target, detailVO.getBaseInfo().getTableName());
        String tableInclude = masterTableInclude(target, tableChangelog);

        writeIfMissing(tableChangelog, buildBusinessTableChangelog(detailVO, target.moduleCode()));
        if (!Files.isRegularFile(masterChangelog)) {
            writeIfMissing(masterChangelog, buildSubChangelogMaster(List.of(tableInclude)));
        } else {
            insertIfMissing(masterChangelog, tableInclude, "    " + tableInclude + "\n", "</databaseChangeLog>");
        }
        messages.add("Prepared business table changelog: " + tableChangelog.normalize());
    }

    private void prepareFrontendComponentRegistrationIfNecessary(GeneratorDetailVO.GeneratorInfo info, Map<String, Object> model, List<String> messages)
            throws IOException {
        if (!shouldRegisterFrontendComponent(info)) {
            return;
        }
        Path registerPath = resolveFrontendRegisterPath(info.getPathWeb(), stringModelValue(model, "frontendModuleName"));
        if (!Files.isRegularFile(registerPath)) {
            return;
        }
        String componentKey = stringModelValue(model, "registeredComponent");
        String businessName = stringModelValue(model, "businessName");
        if (componentKey.isBlank() || businessName.isBlank()) {
            return;
        }
        if (insertFrontendComponentRegistrationIfMissing(registerPath, componentKey, businessName)) {
            messages.add("Prepared frontend component registration: " + registerPath);
        }
    }

    /**
     * 磁盘校验
     *
     * @param tableName
     *            表名
     * @return 校验信息
     */
    @Override
    public GenCheckedInfoVO checkDist(String tableName) {
        GeneratorDetailVO detailVO = detail(tableName);
        String pathApi = detailVO.getGeneratorInfo().getPathApi();
        String pathWeb = detailVO.getGeneratorInfo().getPathWeb();
        boolean apiPathExists = FileUtils.isPathExists(pathApi);
        boolean newBackendModule = "new".equals(detailVO.getGeneratorInfo().getBackendTargetType());
        boolean webPathExists = FileUtils.isPathExists(pathWeb);
        GenCheckedInfoVO checkedInfo = new GenCheckedInfoVO();
        // 如果选择模版包含前端，进行前端校验
        if (("all").equals(detailVO.getGeneratorInfo().getGenerateType())) {
            checkedInfo.setCheckedWebPath(webPathExists);
        }
        checkedInfo.setCheckedApiPath(apiPathExists || newBackendModule);
        checkedInfo.setPathApi(pathApi);
        checkedInfo.setPathWeb(pathWeb);
        if (!apiPathExists && !newBackendModule) {
            checkedInfo.getErrors().add("后端生成路径不存在：" + pathApi);
        }
        if (("all").equals(detailVO.getGeneratorInfo().getGenerateType()) && !webPathExists) {
            checkedInfo.getErrors().add("前端生成路径不存在：" + pathWeb);
        }
        validateNewBackendModule(detailVO, checkedInfo);
        validateBackendModule(detailVO, checkedInfo);
        validateStartupScanPackage(detailVO, checkedInfo);
        validateDataScope(detailVO, checkedInfo);
        return checkedInfo;
    }

    @Override
    @Transactional
    public byte[] downloadZip(SelectTablesDTO dto) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        Set<String> zipEntries = new HashSet<>();
        List<GeneratorPreviewVO> planItems = new ArrayList<>();
        try {
            List<String> tableNames = dto.getTableNames();
            // 根据id 获取要导入的表detail
            List<GeneratorDetailVO> detailVOS = getDetailsForTables(tableNames);

            for (GeneratorDetailVO detailVO : detailVOS) {
                planItems.addAll(buildPreviewPlan(detailVO));
            }
            List<GeneratorPreviewVO> zipItems = new ArrayList<>(planItems);
            buildModificationGuidePreviews(planItems).forEach(zipItems::add);
            for (GeneratorPreviewVO item : zipItems) {
                if (OP_CREATE_FILE.equals(item.getOperationType()) || OP_SCRIPT.equals(item.getOperationType())) {
                    addContentToZip(zip, item.getRelativePath(), item.getContent(), zipEntries);
                }
            }
        } finally {
            zip.close();
        }
        return outputStream.toByteArray();
    }

    @Override
    @Transactional
    public List<GeneratorPreviewVO> preview(String tableName) throws IOException {
        GeneratorDetailVO detailVO = detail(tableName);
        List<GeneratorPreviewVO> planItems = buildPreviewPlan(detailVO);
        List<GeneratorPreviewVO> previewItems = new ArrayList<>(planItems);
        previewItems.addAll(buildModificationGuidePreviews(planItems));
        return previewItems;
    }

    private List<GeneratorPreviewVO> buildPreviewPlan(GeneratorDetailVO detailVO) throws IOException {
        List<GeneratorPreviewVO> previews = new ArrayList<>();
        String rootPathApi = detailVO.getGeneratorInfo().getPathApi();
        String rootPathWeb = detailVO.getGeneratorInfo().getPathWeb();
        Map<String, Object> model = new CodeModelBuilder().builderBaseInfo(detailVO).builderImportPackage(detailVO).builderDynamicsParam(detailVO)
                .builderPojo(detailVO).builderVue(detailVO).getModel();

        previews.addAll(buildBackendModulePreviewItems(detailVO.getGeneratorInfo()));
        addBusinessTableChangelogPreviewItems(previews, detailVO);
        // 处理 API 模板
        handleTemplates(BuildTemplateUtils.getApiTemplates(configurer, rootPathApi, detailVO, model), previews, model);
        // 处理 Web 模板
        handleTemplates(BuildTemplateUtils.getWebTemplates(configurer, rootPathWeb, detailVO, model), previews, model);
        addFrontendComponentRegistrationPreviewItems(previews, detailVO.getGeneratorInfo(), model);
        // 处理脚本模板
        if (shouldInitializeMenu(detailVO)) {
            List<MenuCreateDTO> menuCreateDTOS = initMenu(detailVO, model, false); // 预览仅生成脚本，不插入菜单
            if (!menuCreateDTOS.isEmpty()) {
                model.put("sysMenuList", menuCreateDTOS);
                addScriptPreviews(previews, scriptExportService.renderMenuInit(model, null), detailVO.getBaseInfo().getTableName());
            }
        }

        return previews;
    }

    @Override
    public GeneratorPathOptionsVO pathOptions() {
        GeneratorPathOptionsVO vo = new GeneratorPathOptionsVO();
        Path projectRoot = resolveProjectRoot();
        String defaultApiPath = resolveDefaultBackendModulePath(projectRoot, generatorProperties.getModuleName(), generatorProperties.getServiceName());
        String configuredApiPath = generatorProperties.getPath() == null ? null : generatorProperties.getPath().getApi();
        String configuredWebPath = generatorProperties.getPath() == null ? null : generatorProperties.getPath().getWeb();
        Path siblingWebPath = projectRoot.getParent() == null ? null : projectRoot.getParent().resolve("sz-admin").normalize();
        List<GeneratorBackendModuleOptionVO> backendModuleOptions = backendModuleScanner.scan(projectRoot);

        for (GeneratorBackendModuleOptionVO option : backendModuleOptions) {
            addPathOption(vo.getApiOptions(), option.getModuleName() + moduleStatusLabel(option), option.getPath());
        }
        addPathOption(vo.getApiOptions(), "当前配置后端路径", configuredApiPath);
        addPathOption(vo.getApiOptions(), "默认后端模块", defaultApiPath);
        addPathOption(vo.getApiOptions(), "旧版 service-admin 路径", projectRoot.resolve("sz-service").resolve("sz-service-admin").normalize().toString());

        addPathOption(vo.getWebOptions(), "当前配置前端路径", configuredWebPath);
        if (siblingWebPath != null) {
            addPathOption(vo.getWebOptions(), "同级 sz-admin 前端路径", siblingWebPath.toString());
        }

        vo.setBackendModuleOptions(backendModuleOptions);
        vo.setDefaultApiPath(firstPath(vo.getApiOptions()));
        vo.setDefaultWebPath(firstPath(vo.getWebOptions()));
        return vo;
    }

    @Override
    public Template getMenuSqlTemplate() throws IOException {
        return configurer.getConfiguration().getTemplate(File.separator + "sql" + File.separator + "menuImport.sql.ftl");
    }

    private void handleTemplates(List<AbstractCodeGenerationTemplate> templates, List<GeneratorPreviewVO> previews, Map<String, Object> model)
            throws IOException {
        for (AbstractCodeGenerationTemplate template : templates) {
            CodeGenTempResult tmpRes = template.buildTemplate(false);
            String relativePath = toTreeRelativePath(Paths.get(tmpRes.getFullPath()), tmpRes.getProjectName(), tmpRes.getOutputRelativePath());
            String fileName = Paths.get(relativePath).getFileName().toString();
            String templateProcess = renderTemplateString(tmpRes, model);
            boolean exists = Files.exists(Paths.get(tmpRes.getFullPath()));

            previews.add(buildPreviewItem(fileName, relativePath, tmpRes.getFullPath(), firstPathSegment(relativePath), exists ? OP_SKIP_EXISTS : OP_CREATE_FILE,
                    tmpRes.getLanguage(), templateProcess, null, tmpRes.getAlias(), exists ? "目标文件已存在，生成器不会覆盖。" : "新增业务代码文件。"));
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

    private void addContentToZip(ZipOutputStream zip, String relativePath, String content) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            ZipEntry zipEntry = new ZipEntry(relativePath);
            zip.putNextEntry(zipEntry);
            IOUtils.copy(inputStream, zip);
            zip.closeEntry();
        }
    }

    private void addContentToZip(ZipOutputStream zip, String relativePath, String content, Set<String> zipEntries) throws IOException {
        String zipPath = uniqueZipPath(normalizeZipPath(relativePath), zipEntries);
        addContentToZip(zip, zipPath, content == null ? "" : content);
    }

    private static void addScriptPreviews(List<GeneratorPreviewVO> previews, ScriptExportVO scriptExport, String tableName) {
        for (ScriptExportItemVO item : scriptExport.getItems()) {
            String relativePath = Paths.get("scripts", tableName, item.getFileName()).toString();
            previews.add(buildPreviewItem(Paths.get(item.getFileName()).getFileName().toString(), relativePath, relativePath, "scripts", OP_SCRIPT, item.getLanguage(),
                    item.getContent(), null, item.getTitle(), "菜单/权限初始化脚本。"));
        }
    }

    private List<GeneratorPreviewVO> buildBackendModulePreviewItems(GeneratorDetailVO.GeneratorInfo info) throws IOException {
        Path projectRoot = resolveProjectRoot();
        boolean newTarget = "new".equals(info.getBackendTargetType());
        Optional<GeneratorBackendModuleOptionVO> existingOption = backendModuleScanner.findByModuleName(projectRoot, info.getBackendModuleName());
        if (!newTarget && (existingOption.isEmpty() || GeneratorBackendModuleScanner.STATUS_READY.equals(existingOption.get().getStatus()))) {
            return List.of();
        }
        String moduleName = newTarget ? normalizeModuleName(info.getBackendModuleName()) : existingOption.get().getModuleName();
        String moduleCode = newTarget ? normalizeModuleCode(moduleName) : existingOption.get().getModuleCode();
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getApiPrefixModule());
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getFrontendModuleName());
        GeneratorBackendModuleOptionVO option = newTarget ? backendModuleScanner.buildNewModuleOption(projectRoot, moduleName, moduleCode, info.getPathApi())
                : existingOption.get();
        String packageName = info.getPackageName() == null || info.getPackageName().isBlank() ? option.getPackageName() : info.getPackageName();
        String apiPrefix = info.getApiPrefix() == null || info.getApiPrefix().isBlank() ? option.getApiPrefix() : info.getApiPrefix();
        Path modulePath = Paths.get(option.getPath()).normalize();
        Path serviceRoot = resolveServiceRoot(projectRoot);
        String classPrefix = upperCamel(moduleCode);
        List<GeneratorPreviewVO> previews = new ArrayList<>();

        addCreatePreview(previews, modulePath.resolve("pom.xml"), buildModulePom(option.getModuleName()), "xml", "模块 POM 文件。");
        addCreatePreview(previews, modulePath.resolve(javaPath(packageName, "config", classPrefix + "ApiPrefixConfiguration.java")),
                buildApiPrefixConfiguration(packageName, classPrefix, moduleCode, apiPrefix), "java", "模块 API 前缀配置。");
        addCreatePreview(previews, modulePath.resolve(javaPath(packageName, "config", classPrefix + "MapperScanConfiguration.java")),
                buildMapperScanConfiguration(packageName, classPrefix), "java", "模块 MapperScan 配置。");
        addCreatePreview(previews, modulePath.resolve(javaPath(packageName, "config", classPrefix + "ExcelTemplateScanConfiguration.java")),
                buildExcelTemplateScanConfiguration(packageName, classPrefix), "java", "模块 Excel 导入模板扫描配置。");
        addCreatePreview(previews, modulePath.resolve("src/main/resources/db/changelog/module-" + moduleCode + "-changelog.xml"), buildModuleChangelog(moduleCode),
                "xml", "模块 Liquibase 入口。");
        addCreatePreview(previews, modulePath.resolve("src/main/resources/db/changelog/" + moduleCode + "/changelog-master.xml"),
                buildSubChangelogMaster(List.of()), "xml", "模块 Liquibase master。");

        addInsertPreview(previews, projectRoot.resolve(generatorProperties.getModuleName()).resolve("pom.xml"), "<module>" + option.getModuleName() + "</module>",
                "    <module>" + option.getModuleName() + "</module>\n", "</modules>", true, "xml", "接入 sz-module 聚合 POM。");
        addDependencyManagementPreview(previews, projectRoot.resolve("pom.xml"), option.getModuleName());
        addInsertPreview(previews, serviceRoot.resolve("pom.xml"), "<artifactId>" + option.getModuleName() + "</artifactId>",
                """
                        <dependency>
                            <groupId>com.sz</groupId>
                            <artifactId>%s</artifactId>
                        </dependency>
                """.formatted(option.getModuleName()), "    </dependencies>", true, "xml", "接入启动服务依赖。");
        addInsertPreview(previews, serviceRoot.resolve("src/main/resources/db/changelog/changelog-master.xml"), "module-" + moduleCode + "-changelog.xml",
                "    <include file=\"db/changelog/module-" + moduleCode + "-changelog.xml\" errorIfMissing=\"false\"/>\n", "</databaseChangeLog>", true, "xml",
                "接入启动服务 Liquibase 主入口。");
        addInsertPreview(previews, serviceRoot.resolve("src/main/resources/application.yml"), "      " + moduleCode + ":",
                "      " + moduleCode + ":\n        enabled: true\n        prefix: " + apiPrefix + "\n", "    modules:", false, "yaml", "接入 API 前缀配置。");
        return previews;
    }

    private void addBusinessTableChangelogPreviewItems(List<GeneratorPreviewVO> previews, GeneratorDetailVO detailVO) throws IOException {
        BackendModuleTarget target = resolveBackendModuleTarget(detailVO.getGeneratorInfo());
        if (target.moduleCode().isBlank() || target.modulePath().toString().isBlank()) {
            return;
        }
        String tableName = detailVO.getBaseInfo().getTableName();
        Path masterChangelog = subChangelogMasterPath(target);
        Path tableChangelog = businessTableChangelogPath(target, tableName);
        String tableInclude = masterTableInclude(target, tableChangelog);

        addCreatePreview(previews, tableChangelog, buildBusinessTableChangelog(detailVO, target.moduleCode()), "xml", "业务表结构 changelog。");
        if (!mergeCreatePreview(previews, masterChangelog, buildSubChangelogMaster(List.of(tableInclude)), "模块 Liquibase master。")) {
            addInsertPreview(previews, masterChangelog, tableInclude, "    " + tableInclude + "\n", "</databaseChangeLog>", true, "xml",
                    "接入业务表结构 changelog。");
        }
    }

    private void addFrontendComponentRegistrationPreviewItems(List<GeneratorPreviewVO> previews, GeneratorDetailVO.GeneratorInfo info,
            Map<String, Object> model) throws IOException {
        if (!shouldRegisterFrontendComponent(info)) {
            return;
        }
        String moduleName = stringModelValue(model, "frontendModuleName");
        String componentKey = stringModelValue(model, "registeredComponent");
        String businessName = stringModelValue(model, "businessName");
        if (moduleName.isBlank() || componentKey.isBlank() || businessName.isBlank()) {
            return;
        }
        Path registerPath = resolveFrontendRegisterPath(info.getPathWeb(), moduleName);
        if (!Files.isRegularFile(registerPath)) {
            return;
        }
        String text = Files.readString(registerPath, StandardCharsets.UTF_8);
        if (text.contains(componentKey)) {
            return;
        }
        Path fullPath = registerPath.toAbsolutePath().normalize();
        String relativePath = toTreeRelativePath(fullPath, Paths.get(info.getPathWeb()).toAbsolutePath().normalize(), "sz-admin",
                Paths.get("src", "modules", moduleName, "register.ts").toString());
        String componentLine = buildFrontendComponentRegisterLine(componentKey, businessName);
        String diff = buildFrontendComponentRegistrationDiff(relativePath, componentLine);
        previews.removeIf(item -> OP_SKIP_EXISTS.equals(item.getOperationType()) && samePath(item.getFullPath(), fullPath.toString()));
        previews.add(buildPreviewItem("register.ts", relativePath, fullPath.toString(), firstPathSegment(relativePath), OP_MODIFY_FILE, "ts", null, diff,
                "register.ts", "Register generated page component in the frontend module."));
    }

    private void addCreatePreview(List<GeneratorPreviewVO> previews, Path path, String content, String language, String message) {
        Path fullPath = path.toAbsolutePath().normalize();
        String relativePath = toTreeRelativePath(fullPath, rootName(path), path.toString());
        boolean exists = Files.exists(fullPath);
        previews.add(buildPreviewItem(fullPath.getFileName().toString(), relativePath, fullPath.toString(), firstPathSegment(relativePath),
                exists ? OP_SKIP_EXISTS : OP_CREATE_FILE, language, content, null, fullPath.getFileName().toString(), exists ? "目标文件已存在，生成器不会覆盖。" : message));
    }

    private static boolean mergeCreatePreview(List<GeneratorPreviewVO> previews, Path path, String content, String message) {
        Path fullPath = path.toAbsolutePath().normalize();
        Optional<GeneratorPreviewVO> preview = previews.stream()
                .filter(item -> OP_CREATE_FILE.equals(item.getOperationType()))
                .filter(item -> samePath(item.getFullPath(), fullPath.toString()))
                .findFirst();
        if (preview.isEmpty()) {
            return false;
        }
        preview.get().setContent(content);
        preview.get().setCode(content);
        preview.get().setMessage(message);
        return true;
    }

    private void addInsertPreview(List<GeneratorPreviewVO> previews, Path path, String existsToken, String insertion, String marker, boolean insertBefore,
            String language, String message) throws IOException {
        Path fullPath = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(fullPath)) {
            return;
        }
        String text = Files.readString(fullPath, StandardCharsets.UTF_8);
        if (text.contains(existsToken)) {
            return;
        }
        String relativePath = toTreeRelativePath(fullPath, rootName(path), path.toString());
        String diff;
        if (text.contains(marker)) {
            diff = buildInsertDiff(relativePath, marker, insertion, insertBefore);
        } else {
            diff = "# 未找到自动插入锚点：" + marker + "\n# 目标文件：" + relativePath + "\n";
        }
        previews.add(buildPreviewItem(fullPath.getFileName().toString(), relativePath, fullPath.toString(), firstPathSegment(relativePath), OP_MODIFY_FILE,
                language, null, diff, fullPath.getFileName().toString(), message));
    }

    private void addDependencyManagementPreview(List<GeneratorPreviewVO> previews, Path path, String moduleName) throws IOException {
        Path fullPath = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(fullPath)) {
            return;
        }
        String text = Files.readString(fullPath, StandardCharsets.UTF_8);
        if (text.contains("<artifactId>" + moduleName + "</artifactId>")) {
            return;
        }
        String relativePath = toTreeRelativePath(fullPath, rootName(path), path.toString());
        String diff;
        if (text.contains("<dependencyManagement>") && text.indexOf("</dependencies>", text.indexOf("<dependencyManagement>")) > 0) {
            diff = buildInsertDiff(relativePath, "dependencyManagement </dependencies>", buildManagedDependency(moduleName), true);
        } else {
            diff = "# 未找到自动插入锚点：dependencyManagement </dependencies>\n# 目标文件：" + relativePath + "\n";
        }
        previews.add(buildPreviewItem(fullPath.getFileName().toString(), relativePath, fullPath.toString(), firstPathSegment(relativePath), OP_MODIFY_FILE,
                "xml", null, diff, fullPath.getFileName().toString(), "接入根 POM 依赖版本管理。"));
    }

    private String toTreeRelativePath(Path fullPath, String fallbackProjectName, String fallbackRelativePath) {
        Path normalized = fullPath.toAbsolutePath().normalize();
        Path projectRoot = resolveProjectRoot().toAbsolutePath().normalize();
        if (normalized.startsWith(projectRoot)) {
            return Paths.get(projectRoot.getFileName().toString(), projectRoot.relativize(normalized).toString()).toString();
        }
        Path webRoot = projectRoot.getParent() == null ? null : projectRoot.getParent().resolve("sz-admin").toAbsolutePath().normalize();
        if (webRoot != null && normalized.startsWith(webRoot)) {
            return Paths.get(webRoot.getFileName().toString(), webRoot.relativize(normalized).toString()).toString();
        }
        return Paths.get(fallbackProjectName, fallbackRelativePath).toString();
    }

    private String toTreeRelativePath(Path fullPath, Path rootPath, String fallbackProjectName, String fallbackRelativePath) {
        Path normalized = fullPath.toAbsolutePath().normalize();
        Path normalizedRoot = rootPath.toAbsolutePath().normalize();
        if (normalized.startsWith(normalizedRoot)) {
            String projectName = normalizedRoot.getFileName() == null ? fallbackProjectName : normalizedRoot.getFileName().toString();
            return Paths.get(projectName, normalizedRoot.relativize(normalized).toString()).toString();
        }
        return toTreeRelativePath(fullPath, fallbackProjectName, fallbackRelativePath);
    }

    private static GeneratorPreviewVO buildPreviewItem(String name, String relativePath, String fullPath, String projectName, String operationType, String language,
            String content, String diff, String alias, String message) {
        GeneratorPreviewVO previewVO = new GeneratorPreviewVO();
        previewVO.setName(name);
        previewVO.setRelativePath(normalizeZipPath(relativePath));
        previewVO.setFullPath(fullPath);
        previewVO.setProjectName(projectName);
        previewVO.setOperationType(operationType);
        previewVO.setLanguage(language);
        previewVO.setContent(content);
        previewVO.setDiff(diff);
        previewVO.setCode(diff == null || diff.isBlank() ? content : diff);
        previewVO.setAlias(alias);
        previewVO.setMessage(message);
        return previewVO;
    }

    private static String buildInsertDiff(String relativePath, String marker, String insertion, boolean insertBefore) {
        StringBuilder builder = new StringBuilder();
        builder.append("--- a/").append(normalizeZipPath(relativePath)).append('\n');
        builder.append("+++ b/").append(normalizeZipPath(relativePath)).append('\n');
        builder.append("@@ ").append(insertBefore ? "before " : "after ").append(marker).append(" @@\n");
        for (String line : insertion.replace("\r\n", "\n").split("\n", -1)) {
            if (!line.isEmpty()) {
                builder.append('+').append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private static List<GeneratorPreviewVO> buildModificationGuidePreviews(List<GeneratorPreviewVO> planItems) {
        List<GeneratorPreviewVO> modifyItems = planItems.stream()
                .filter(item -> OP_MODIFY_FILE.equals(item.getOperationType()))
                .filter(item -> item.getDiff() != null && !item.getDiff().isBlank())
                .toList();
        if (modifyItems.isEmpty()) {
            return List.of();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("代码生成修改说明\n");
        builder.append("================\n\n");
        builder.append("以下文件需要在现有项目中手动确认并修改。生成器不会在下载包中覆盖这些文件。\n");
        builder.append("请按每个文件下方的说明和 diff 内容，将新增内容插入到对应位置。\n\n");
        for (int i = 0; i < modifyItems.size(); i++) {
            GeneratorPreviewVO item = modifyItems.get(i);
            builder.append(i + 1).append(". ").append(item.getRelativePath()).append('\n');
            if (item.getMessage() != null && !item.getMessage().isBlank()) {
                builder.append("说明：").append(item.getMessage()).append('\n');
            }
            builder.append("修改内容：\n");
            builder.append(item.getDiff()).append('\n');
        }
        String content = builder.toString();
        return List.of(buildPreviewItem("修改说明.txt", "修改说明.txt", "修改说明.txt", "修改说明.txt", OP_SCRIPT, "text", content, null,
                "修改说明.txt", "下载包修改项说明，用户按此文件手动调整现有文件。"));
    }

    private static String normalizeZipPath(String path) {
        return path == null ? "" : path.replace('\\', '/');
    }

    private static String uniqueZipPath(String path, Set<String> zipEntries) {
        String candidate = path;
        int index = 2;
        while (!zipEntries.add(candidate)) {
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex > path.lastIndexOf('/')) {
                candidate = path.substring(0, dotIndex) + "-" + index + path.substring(dotIndex);
            } else {
                candidate = path + "-" + index;
            }
            index++;
        }
        return candidate;
    }

    private static String firstPathSegment(String path) {
        String normalized = normalizeZipPath(path);
        int index = normalized.indexOf('/');
        return index < 0 ? normalized : normalized.substring(0, index);
    }

    private static String rootName(Path path) {
        Path normalized = path.normalize();
        return normalized.getFileName() == null ? normalized.toString() : normalized.getFileName().toString();
    }

    private static void addPathOption(List<GeneratorPathOptionsVO.PathOption> options, String label, String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        boolean exists = options.stream().anyMatch(option -> path.equals(option.getPath()));
        if (!exists) {
            options.add(new GeneratorPathOptionsVO.PathOption(label, path));
        }
    }

    private static String firstPath(List<GeneratorPathOptionsVO.PathOption> options) {
        return options.isEmpty() ? "" : options.get(0).getPath();
    }

    private String resolveDefaultBackendModulePath(Path projectRoot, String moduleName, String serviceName) {
        List<GeneratorBackendModuleOptionVO> options = backendModuleScanner.scan(projectRoot);
        return options.stream().filter(option -> Boolean.TRUE.equals(option.getRecommended())).findFirst().map(GeneratorBackendModuleOptionVO::getPath)
                .orElse(projectRoot.resolve(moduleName).resolve(serviceName).normalize().toString());
    }

    private void applyDefaultModuleInfo(GeneratorTable generatorTable) {
        Path projectRoot = resolveProjectRoot();
        List<GeneratorBackendModuleOptionVO> options = backendModuleScanner.scan(projectRoot);
        Optional<GeneratorBackendModuleOptionVO> selected = options.stream().filter(option -> generatorTable.getPathApi() != null
                && Paths.get(generatorTable.getPathApi()).normalize().toString().equals(Paths.get(option.getPath()).normalize().toString())).findFirst();
        if (selected.isEmpty()) {
            selected = options.stream().filter(option -> Boolean.TRUE.equals(option.getRecommended())).findFirst();
        }
        selected.ifPresent(option -> {
            generatorTable.setBackendTargetType("existing");
            generatorTable.setBackendModuleName(option.getModuleName());
            generatorTable.setPackageName(option.getPackageName());
            generatorTable.setApiPrefixModule(option.getApiPrefixModule());
            generatorTable.setApiPrefix(option.getApiPrefix());
            generatorTable.setFrontendLayout("module");
            generatorTable.setFrontendModuleName(option.getModuleCode());
        });
    }

    private void validateBackendModule(GeneratorDetailVO detailVO, GenCheckedInfoVO checkedInfo) {
        GeneratorDetailVO.GeneratorInfo info = detailVO.getGeneratorInfo();
        if ("new".equals(info.getBackendTargetType())) {
            return;
        }
        if (info.getBackendModuleName() == null || info.getBackendModuleName().isBlank()) {
            checkedInfo.getWarnings().add("未选择后端模块，生成器将仅按路径生成代码。");
            return;
        }
        Optional<GeneratorBackendModuleOptionVO> option = backendModuleScanner.findByModuleName(resolveProjectRoot(), info.getBackendModuleName());
        if (option.isEmpty()) {
            checkedInfo.setCheckedBackendModule(false);
            checkedInfo.getErrors().add("后端模块不存在：" + info.getBackendModuleName());
            return;
        }
        if (!GeneratorBackendModuleScanner.STATUS_READY.equals(option.get().getStatus())) {
            checkedInfo.getWarnings().add("后端模块将自动补齐接入项：" + String.join("、", option.get().getMissingItems()));
        }
    }

    private void validateStartupScanPackage(GeneratorDetailVO detailVO, GenCheckedInfoVO checkedInfo) {
        GeneratorDetailVO.GeneratorInfo info = detailVO.getGeneratorInfo();
        if (!needsBackendTemplates(info)) {
            return;
        }
        String packageName = resolveGeneratorPackageName(info);
        if (packageName.isBlank()) {
            return;
        }
        Path serviceRoot = resolveServiceRoot(resolveProjectRoot());
        List<String> scanPackages = resolveStartupScanPackages(serviceRoot);
        if (scanPackages.isEmpty()) {
            checkedInfo.getWarnings().add("未识别到启动类扫描范围，无法确认包名 " + packageName + " 是否会被 Spring Boot 扫描。");
            return;
        }
        if (!isPackageCoveredByScanPackages(packageName, scanPackages)) {
            checkedInfo.setCheckedBackendModule(false);
            checkedInfo.getErrors().add("后端包名 " + packageName + " 不在启动服务扫描范围内（" + String.join("、", scanPackages)
                    + "）。生成的 API 前缀、MapperScan、Excel 扫描配置不会被 Spring Boot 加载；请将包名调整到扫描包下，或在启动类配置 scanBasePackages。");
        }
    }

    private String resolveGeneratorPackageName(GeneratorDetailVO.GeneratorInfo info) {
        if (info.getPackageName() != null && !info.getPackageName().isBlank()) {
            return info.getPackageName().trim();
        }
        Path projectRoot = resolveProjectRoot();
        if ("new".equals(info.getBackendTargetType())) {
            String moduleName = normalizeModuleName(info.getBackendModuleName());
            String moduleCode = normalizeModuleCode(moduleName);
            if (moduleCode.isBlank()) {
                return "";
            }
            return backendModuleScanner.buildNewModuleOption(projectRoot, moduleName, moduleCode, info.getPathApi()).getPackageName();
        }
        if (info.getBackendModuleName() == null || info.getBackendModuleName().isBlank()) {
            return "";
        }
        return backendModuleScanner.findByModuleName(projectRoot, info.getBackendModuleName()).map(GeneratorBackendModuleOptionVO::getPackageName).orElse("");
    }

    private static boolean needsBackendTemplates(GeneratorDetailVO.GeneratorInfo info) {
        return info.getGenerateType() == null || Set.of("all", "server", "service", "db").contains(info.getGenerateType());
    }

    private void validateNewBackendModule(GeneratorDetailVO detailVO, GenCheckedInfoVO checkedInfo) {
        GeneratorDetailVO.GeneratorInfo info = detailVO.getGeneratorInfo();
        if (!"new".equals(info.getBackendTargetType())) {
            return;
        }
        String moduleName = normalizeModuleName(info.getBackendModuleName());
        String moduleCode = normalizeModuleCode(moduleName);
        if (moduleName.isBlank()) {
            checkedInfo.setCheckedBackendModule(false);
            checkedInfo.getErrors().add("请填写新模块名。");
            return;
        }
        if (info.getPathApi() == null || info.getPathApi().isBlank()) {
            checkedInfo.setCheckedApiPath(false);
            checkedInfo.getErrors().add("请填写新模块 api 项目路径。");
            return;
        }
        Optional<String> conflict = findNewBackendModuleConflict(resolveProjectRoot(), moduleName, moduleCode, Paths.get(info.getPathApi()));
        conflict.ifPresent(message -> {
            checkedInfo.setCheckedBackendModule(false);
            checkedInfo.setCheckedApiPath(false);
            checkedInfo.getErrors().add(message);
        });
    }

    private void validateDataScope(GeneratorDetailVO detailVO, GenCheckedInfoVO checkedInfo) {
        if (!"1".equals(detailVO.getGeneratorInfo().getBtnDataScopeType())) {
            return;
        }
        String logicMinUnit = environment.getProperty("sz.data-scope.logic-min-unit", "user");
        String requiredColumn = "dept".equalsIgnoreCase(logicMinUnit) ? "dept_scope" : "create_id";
        boolean hasRequiredColumn = detailVO.getColumns().stream().anyMatch(column -> requiredColumn.equals(column.getColumnName()));
        if (!hasRequiredColumn) {
            checkedInfo.setCheckedDataScope(false);
            checkedInfo.getErrors().add("已开启数据权限，但当前表缺少字段：" + requiredColumn + "。请补充字段或关闭数据权限。");
        }
    }

    private static String moduleStatusLabel(GeneratorBackendModuleOptionVO option) {
        return switch (option.getStatus()) {
            case GeneratorBackendModuleScanner.STATUS_READY -> "（已接入）";
            case GeneratorBackendModuleScanner.STATUS_PENDING -> "（将自动补齐）";
            default -> "（不可用）";
        };
    }

    private static void writeIfMissing(Path path, String content) throws IOException {
        if (Files.exists(path)) {
            return;
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    private static void insertIfMissing(Path path, String existsToken, String insertion, String beforeToken) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Module bootstrap file not found: " + path);
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        if (text.contains(existsToken)) {
            return;
        }
        int index = text.indexOf(beforeToken);
        if (index < 0) {
            throw new IOException("Module bootstrap anchor not found: " + beforeToken + ", file: " + path);
        }
        String updated = text.substring(0, index) + insertion + text.substring(index);
        Files.writeString(path, updated, StandardCharsets.UTF_8);
    }

    private static void insertDependencyManagementIfMissing(Path path, String moduleName) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Root POM file not found: " + path);
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        if (text.contains("<artifactId>" + moduleName + "</artifactId>")) {
            return;
        }
        int dependencyManagementIndex = text.indexOf("<dependencyManagement>");
        if (dependencyManagementIndex < 0) {
            throw new IOException("Root POM dependencyManagement anchor not found, file: " + path);
        }
        int dependenciesIndex = text.indexOf("<dependencies>", dependencyManagementIndex);
        int dependenciesEndIndex = text.indexOf("</dependencies>", dependenciesIndex);
        if (dependenciesIndex < 0 || dependenciesEndIndex < 0) {
            throw new IOException("Root POM dependencyManagement dependencies anchor not found, file: " + path);
        }
        String updated = text.substring(0, dependenciesEndIndex) + buildManagedDependency(moduleName) + text.substring(dependenciesEndIndex);
        Files.writeString(path, updated, StandardCharsets.UTF_8);
    }

    private static void insertApiPrefixIfMissing(Path path, String moduleCode, String apiPrefix) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("API prefix config file not found: " + path);
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        String token = "      " + moduleCode + ":";
        if (text.contains(token)) {
            return;
        }
        String marker = "    modules:";
        int index = text.indexOf(marker);
        if (index < 0) {
            throw new IOException("API prefix config anchor not found: " + marker + ", file: " + path);
        }
        int lineEnd = text.indexOf('\n', index);
        if (lineEnd < 0) {
            throw new IOException("API prefix config anchor line is invalid: " + path);
        }
        String lineSeparator = text.contains("\r\n") ? "\r\n" : "\n";
        String insertion = "      " + moduleCode + ":" + lineSeparator + "        enabled: true" + lineSeparator + "        prefix: " + apiPrefix
                + lineSeparator;
        int insertIndex = lineEnd + 1;
        String updated = text.substring(0, insertIndex) + insertion + text.substring(insertIndex);
        Files.writeString(path, updated, StandardCharsets.UTF_8);
    }

    private static boolean insertFrontendComponentRegistrationIfMissing(Path path, String componentKey, String businessName) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        if (text.contains(componentKey)) {
            return false;
        }
        String updated = insertFrontendComponentLine(text, buildFrontendComponentRegisterLine(componentKey, businessName), path);
        if (updated.equals(text)) {
            return false;
        }
        Files.writeString(path, updated, StandardCharsets.UTF_8);
        return true;
    }

    private static boolean shouldRegisterFrontendComponent(GeneratorDetailVO.GeneratorInfo info) {
        return "all".equals(info.getGenerateType()) && !"legacy".equals(info.getFrontendLayout());
    }

    private static Path resolveFrontendRegisterPath(String pathWeb, String moduleName) {
        return Paths.get(pathWeb == null ? "" : pathWeb).resolve("src").resolve("modules").resolve(moduleName).resolve("register.ts").normalize();
    }

    private static String buildFrontendComponentRegisterLine(String componentKey, String businessName) {
        return "    '" + componentKey + "': () => import('./views/" + businessName + "/index.vue')";
    }

    private static String buildFrontendComponentRegistrationDiff(String relativePath, String componentLine) {
        StringBuilder builder = new StringBuilder();
        builder.append("--- a/").append(normalizeZipPath(relativePath)).append('\n');
        builder.append("+++ b/").append(normalizeZipPath(relativePath)).append('\n');
        builder.append("@@ components @@\n");
        builder.append('+').append(componentLine).append('\n');
        return builder.toString();
    }

    private static String insertFrontendComponentLine(String text, String componentLine, Path path) throws IOException {
        String lineSeparator = resolveLineSeparator(text);
        int componentsIndex = text.indexOf("components:");
        if (componentsIndex >= 0) {
            int openBraceIndex = text.indexOf('{', componentsIndex);
            int closeBraceIndex = findMatchingBrace(text, openBraceIndex);
            if (openBraceIndex < 0 || closeBraceIndex < 0) {
                throw new IOException("Frontend module components block is invalid: " + path);
            }
            String beforeClose = text.substring(0, closeBraceIndex);
            int previousContentEnd = previousContentEnd(beforeClose);
            StringBuilder builder = new StringBuilder(text);
            if (previousContentEnd > openBraceIndex && builder.charAt(previousContentEnd) != ',' && builder.charAt(previousContentEnd) != '{') {
                builder.insert(previousContentEnd + 1, ',');
                closeBraceIndex++;
            }
            int closeLineStart = builder.lastIndexOf("\n", closeBraceIndex - 1) + 1;
            builder.insert(closeLineStart, componentLine + lineSeparator);
            return builder.toString();
        }

        int nameIndex = text.indexOf("name:");
        if (nameIndex < 0) {
            throw new IOException("Frontend module name anchor not found: " + path);
        }
        int insertIndex = lineEndIndex(text, nameIndex);
        String insertion = "  components: {" + lineSeparator + componentLine + lineSeparator + "  }," + lineSeparator;
        return text.substring(0, insertIndex) + insertion + text.substring(insertIndex);
    }

    private static int findMatchingBrace(String text, int openBraceIndex) {
        if (openBraceIndex < 0) {
            return -1;
        }
        int depth = 0;
        char quote = 0;
        boolean escaping = false;
        for (int i = openBraceIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                if (escaping) {
                    escaping = false;
                } else if (c == '\\') {
                    escaping = true;
                } else if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '\'' || c == '"' || c == '`') {
                quote = c;
                continue;
            }
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int previousContentEnd(String text) {
        for (int i = text.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int lineEndIndex(String text, int startIndex) {
        int lineEnd = text.indexOf('\n', startIndex);
        return lineEnd < 0 ? text.length() : lineEnd + 1;
    }

    private static String resolveLineSeparator(String text) {
        return text.contains("\r\n") ? "\r\n" : "\n";
    }

    private static String stringModelValue(Map<String, Object> model, String key) {
        Object value = model.get(key);
        return value == null ? "" : value.toString();
    }

    private static boolean samePath(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return Paths.get(left).toAbsolutePath().normalize().equals(Paths.get(right).toAbsolutePath().normalize());
    }

    private static Path javaPath(String packageName, String subPackage, String fileName) {
        return Paths.get("src/main/java", packageName.replace(".", File.separator), subPackage, fileName);
    }

    private static String buildModulePom(String moduleName) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <artifactId>sz-module</artifactId>
                        <groupId>com.sz</groupId>
                        <version>${revision}</version>
                    </parent>
                    <artifactId>%s</artifactId>

                    <dependencies>
                        <dependency>
                            <groupId>com.sz</groupId>
                            <artifactId>sz-common-core</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.sz</groupId>
                            <artifactId>sz-common-log</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.sz</groupId>
                            <artifactId>sz-common-db-core</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.mybatis-flex</groupId>
                            <artifactId>mybatis-flex-core</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.mybatis-flex</groupId>
                            <artifactId>mybatis-flex-annotation</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.mybatis-flex</groupId>
                            <artifactId>mybatis-flex-spring</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.mybatis</groupId>
                            <artifactId>mybatis</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.mybatis</groupId>
                            <artifactId>mybatis-spring</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>cn.dev33</groupId>
                            <artifactId>sa-token-core</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                            <scope>provided</scope>
                            <optional>true</optional>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-context</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-tx</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>jakarta.validation</groupId>
                            <artifactId>jakarta.validation-api</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <scope>provided</scope>
                            <optional>true</optional>
                        </dependency>
                        <dependency>
                            <groupId>io.swagger.core.v3</groupId>
                            <artifactId>swagger-annotations</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.sz.excel</groupId>
                            <artifactId>sz-common-excel</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>cn.idev.excel</groupId>
                            <artifactId>fastexcel-core</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.sz.resource</groupId>
                            <artifactId>sz-common-resource</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """.formatted(moduleName);
    }

    private static String buildManagedDependency(String moduleName) {
        return """
                            <dependency>
                                <groupId>com.sz</groupId>
                                <artifactId>%s</artifactId>
                                <version>${project.version}</version>
                            </dependency>
                """.formatted(moduleName);
    }

    private static String buildApiPrefixConfiguration(String packageName, String classPrefix, String moduleCode, String apiPrefix) {
        return """
                package %s.config;

                import com.sz.core.common.web.ApiPrefixRegister;
                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;

                /**
                 * %s API 前缀声明。
                 */
                @Configuration
                public class %sApiPrefixConfiguration {

                    @Bean
                    public ApiPrefixRegister %sApiPrefixRegister() {
                        return new ApiPrefixRegister() {

                            @Override
                            public String module() {
                                return "%s";
                            }

                            @Override
                            public String prefix() {
                                return "%s";
                            }

                            @Override
                            public String[] basePackages() {
                                return new String[]{"%s"};
                            }
                        };
                    }
                }
                """.formatted(packageName, classPrefix, classPrefix, lowerCamel(classPrefix), moduleCode, apiPrefix, packageName);
    }

    private static String buildMapperScanConfiguration(String packageName, String classPrefix) {
        return """
                package %s.config;

                import org.mybatis.spring.annotation.MapperScan;
                import org.springframework.context.annotation.Configuration;

                /**
                 * %s 模块自治 MapperScan。
                 */
                @Configuration
                @MapperScan(basePackages = "%s")
                public class %sMapperScanConfiguration {
                }
                """.formatted(packageName, classPrefix, packageName + ".*.mapper", classPrefix);
    }

    private static String buildExcelTemplateScanConfiguration(String packageName, String classPrefix) {
        return """
                package %s.config;

                import com.sz.excel.annotation.EnableExcelTemplateScan;
                import org.springframework.context.annotation.Configuration;

                /**
                 * %s 模块 Excel 导入模板扫描。
                 */
                @Configuration
                @EnableExcelTemplateScan(basePackages = "%s")
                public class %sExcelTemplateScanConfiguration {
                }
                """.formatted(packageName, classPrefix, packageName, classPrefix);
    }

    private static String buildModuleChangelog(String moduleCode) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">
                    <include file="%s/changelog-master.xml" relativeToChangelogFile="true"/>
                </databaseChangeLog>
                """.formatted(moduleCode);
    }

    private static String buildSubChangelogMaster(List<String> includes) {
        StringBuilder builder = new StringBuilder();
        builder.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">
                </databaseChangeLog>
                """);
        int insertIndex = builder.lastIndexOf("</databaseChangeLog>");
        includes.forEach(include -> builder.insert(insertIndex, "    " + include + "\n"));
        return builder.toString();
    }

    private BackendModuleTarget resolveBackendModuleTarget(GeneratorDetailVO.GeneratorInfo info) {
        Path projectRoot = resolveProjectRoot();
        boolean newTarget = "new".equals(info.getBackendTargetType());
        Optional<GeneratorBackendModuleOptionVO> existingOption = backendModuleScanner.findByModuleName(projectRoot, info.getBackendModuleName());
        String moduleName = newTarget ? normalizeModuleName(info.getBackendModuleName())
                : existingOption.map(GeneratorBackendModuleOptionVO::getModuleName).orElse(info.getBackendModuleName());
        String moduleCode = newTarget ? normalizeModuleCode(moduleName)
                : existingOption.map(GeneratorBackendModuleOptionVO::getModuleCode).orElse(normalizeModuleCode(moduleName));
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getApiPrefixModule());
        if (moduleCode.isBlank()) moduleCode = normalizeModuleCode(info.getFrontendModuleName());
        Path modulePath = info.getPathApi() == null || info.getPathApi().isBlank() ? Path.of("") : Paths.get(info.getPathApi()).normalize();
        return new BackendModuleTarget(moduleName, moduleCode, modulePath, DEFAULT_CHANGELOG_VERSION);
    }

    private static Path businessTableChangelogPath(BackendModuleTarget target, String tableName) {
        return target.modulePath().resolve("src/main/resources/db/changelog/" + target.moduleCode() + "/" + target.changelogDirectory() + "/"
                + nextBusinessTableChangelogFileName(target, tableName));
    }

    private static Path subChangelogMasterPath(BackendModuleTarget target) {
        return target.modulePath().resolve("src/main/resources/db/changelog/" + target.moduleCode() + "/changelog-master.xml");
    }

    private static String masterTableInclude(BackendModuleTarget target, Path tableChangelog) {
        Path masterRoot = subChangelogMasterPath(target).getParent().normalize();
        String relativePath = masterRoot.relativize(tableChangelog.normalize()).toString().replace('\\', '/');
        return "<include file=\"" + relativePath + "\" relativeToChangelogFile=\"true\"/>";
    }

    private static String nextBusinessTableChangelogFileName(BackendModuleTarget target, String tableName) {
        Path versionRoot = target.modulePath().resolve("src/main/resources/db/changelog/" + target.moduleCode() + "/" + target.changelogDirectory());
        String suffix = "_" + tableName + ".xml";
        if (!Files.isDirectory(versionRoot)) {
            return "001" + suffix;
        }
        try (var stream = Files.list(versionRoot)) {
            Optional<String> existing = stream.map(path -> path.getFileName().toString())
                    .filter(name -> name.matches("\\d{3}_" + java.util.regex.Pattern.quote(tableName) + "\\.xml"))
                    .findFirst();
            if (existing.isPresent()) {
                return existing.get();
            }
        } catch (IOException ignored) {
            return "001" + suffix;
        }
        int maxIndex = 0;
        try (var stream = Files.list(versionRoot)) {
            maxIndex = stream.map(path -> path.getFileName().toString())
                    .filter(name -> name.matches("\\d{3}_.+\\.xml"))
                    .mapToInt(name -> Integer.parseInt(name.substring(0, 3)))
                    .max()
                    .orElse(0);
        } catch (IOException ignored) {
            return "001" + suffix;
        }
        return "%03d%s".formatted(maxIndex + 1, suffix);
    }

    private static String buildBusinessTableChangelog(GeneratorDetailVO detailVO, String moduleCode) {
        String tableName = detailVO.getBaseInfo().getTableName();
        StringBuilder builder = new StringBuilder();
        builder.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

                    <!-- 公共类型定义：保持 MySQL/PostgreSQL 兼容。 -->
                    <property name="datetime.type" value="DATETIME" dbms="mysql"/>
                    <property name="datetime.type" value="TIMESTAMP" dbms="postgresql"/>
                    <property name="json.type" value="JSON" dbms="mysql"/>
                    <property name="json.type" value="JSONB" dbms="postgresql"/>
                    <property name="bool.type" value="CHAR(1)" dbms="mysql"/>
                    <property name="bool.type" value="VARCHAR(1)" dbms="postgresql"/>

                """);
        builder.append("    <changeSet id=\"").append(xmlEscape(moduleCode)).append("-create-").append(xmlEscape(tableName)).append("\" author=\"generator\">\n");
        builder.append("        <preConditions onFail=\"MARK_RAN\">\n");
        builder.append("            <not><tableExists tableName=\"").append(xmlEscape(tableName)).append("\"/></not>\n");
        builder.append("        </preConditions>\n");
        builder.append("        <createTable tableName=\"").append(xmlEscape(tableName)).append("\" remarks=\"")
                .append(xmlEscape(detailVO.getBaseInfo().getTableComment())).append("\">\n");
        for (GeneratorDetailVO.Column column : detailVO.getColumns()) {
            builder.append("            <column name=\"").append(xmlEscape(column.getColumnName())).append("\" type=\"")
                    .append(xmlEscape(toLiquibaseColumnType(column.getColumnType()))).append("\"");
            if ("1".equals(column.getIsIncrement())) {
                builder.append(" autoIncrement=\"true\"");
            }
            builder.append(" remarks=\"").append(xmlEscape(column.getColumnComment())).append("\"");
            String constraints = buildColumnConstraints(column, tableName);
            if (constraints.isBlank()) {
                builder.append("/>\n");
            } else {
                builder.append(">").append(constraints).append("</column>\n");
            }
        }
        builder.append("        </createTable>\n");
        for (GeneratorDetailVO.Column column : detailVO.getColumns()) {
            if (!"1".equals(column.getIsUniqueValid()) || "1".equals(column.getIsPk())) {
                continue;
            }
            builder.append("        <createIndex tableName=\"").append(xmlEscape(tableName)).append("\" indexName=\"")
                    .append(xmlEscape(indexName("uk", tableName, column.getColumnName()))).append("\" unique=\"true\">\n");
            builder.append("            <column name=\"").append(xmlEscape(column.getColumnName())).append("\"/>\n");
            builder.append("        </createIndex>\n");
        }
        builder.append("    </changeSet>\n");
        builder.append("</databaseChangeLog>\n");
        return builder.toString();
    }

    private static String buildColumnConstraints(GeneratorDetailVO.Column column, String tableName) {
        List<String> attributes = new ArrayList<>();
        if ("1".equals(column.getIsPk())) {
            attributes.add("primaryKey=\"true\"");
            attributes.add("primaryKeyName=\"" + xmlEscape(indexName("pk", tableName, null)) + "\"");
            attributes.add("nullable=\"false\"");
        } else if ("1".equals(column.getIsRequired())) {
            attributes.add("nullable=\"false\"");
        }
        return attributes.isEmpty() ? "" : "<constraints " + String.join(" ", attributes) + "/>";
    }

    private static String toLiquibaseColumnType(String columnType) {
        if (columnType == null || columnType.isBlank()) {
            return "VARCHAR(255)";
        }
        String normalized = columnType.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("datetime") || normalized.startsWith("timestamp")) {
            return "${datetime.type}";
        }
        if (normalized.equals("json") || normalized.equals("jsonb")) {
            return "${json.type}";
        }
        if (normalized.equals("tinyint(1)") || normalized.equals("bit(1)") || normalized.equals("bool") || normalized.equals("boolean")) {
            return "${bool.type}";
        }
        if (normalized.startsWith("int(") || normalized.equals("integer")) {
            return "INT";
        }
        if (normalized.startsWith("bigint(")) {
            return "BIGINT";
        }
        if (normalized.startsWith("tinyint(")) {
            return "TINYINT";
        }
        if (normalized.startsWith("double")) {
            return "DOUBLE";
        }
        return columnType.trim().toUpperCase(Locale.ROOT);
    }

    private static String indexName(String prefix, String tableName, String columnName) {
        String value = prefix + "_" + tableName + (columnName == null || columnName.isBlank() ? "" : "_" + columnName);
        value = value.replaceAll("[^A-Za-z0-9_]", "_").toLowerCase(Locale.ROOT);
        return value.length() <= 60 ? value : value.substring(0, 60);
    }

    private static String xmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String normalizeModuleCode(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().replaceFirst("^sz-module-", "").replace("_", "-").toLowerCase(Locale.ROOT);
    }

    private static String normalizeModuleName(String value) {
        return value == null ? "" : value.trim();
    }

    private record BackendModuleTarget(String moduleName, String moduleCode, Path modulePath, String changelogDirectory) {
    }

    private Optional<String> findNewBackendModuleConflict(Path projectRoot, String moduleName, String moduleCode, Path modulePath) {
        String normalizedModuleName = normalizeModuleName(moduleName);
        String normalizedModuleCode = normalizeModuleCode(moduleCode);
        Path normalizedModulePath = modulePath.toAbsolutePath().normalize();
        Path moduleRoot = projectRoot.resolve(generatorProperties.getModuleName()).normalize();
        if (Files.exists(normalizedModulePath)) {
            if (isSameNewModuleTarget(normalizedModulePath, normalizedModuleName, normalizedModuleCode)) {
                return Optional.empty();
            }
            return Optional.of("新建模块路径已存在：" + normalizedModulePath + "，请切换为现有模块或更换路径。");
        }
        if (!Files.isDirectory(moduleRoot)) {
            return Optional.empty();
        }
        try (var stream = Files.list(moduleRoot)) {
            return stream.filter(Files::isDirectory).map(path -> {
                        String existingModuleName = path.getFileName().toString();
                        String existingModuleCode = normalizeModuleCode(existingModuleName);
                        boolean sameName = existingModuleName.equalsIgnoreCase(normalizedModuleName);
                        boolean sameCode = !normalizedModuleCode.isBlank() && existingModuleCode.equals(normalizedModuleCode);
                        boolean samePath = path.toAbsolutePath().normalize().equals(normalizedModulePath);
                        if (sameName || sameCode || samePath) {
                            if (samePath && isSameNewModuleTarget(path.toAbsolutePath().normalize(), normalizedModuleName, normalizedModuleCode)) {
                                return null;
                            }
                            return "新建模块已存在：" + existingModuleName + "，请切换为现有模块或更换模块名。";
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst();
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private static boolean isSameNewModuleTarget(Path modulePath, String normalizedModuleName, String normalizedModuleCode) {
        Path fileName = modulePath.getFileName();
        if (fileName == null) {
            return false;
        }
        String existingModuleName = fileName.toString();
        String existingModuleCode = normalizeModuleCode(existingModuleName);
        return existingModuleName.equalsIgnoreCase(normalizedModuleName)
                || (!normalizedModuleCode.isBlank() && existingModuleCode.equals(normalizedModuleCode));
    }

    private static String upperCamel(String value) {
        StringBuilder builder = new StringBuilder();
        boolean upperNext = true;
        for (char c : value.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                upperNext = true;
                continue;
            }
            builder.append(upperNext ? Character.toUpperCase(c) : c);
            upperNext = false;
        }
        return builder.isEmpty() ? "Demo" : builder.toString();
    }

    private static String lowerCamel(String value) {
        if (value == null || value.isBlank()) {
            return "demo";
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private static Path resolveProjectRoot() {
        Path path = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (path.getFileName() != null && path.getFileName().toString().startsWith("sz-service-") && path.getParent() != null
                && path.getParent().getParent() != null) {
            return path.getParent().getParent();
        }
        return path;
    }

    private static Path resolveServiceRoot(Path projectRoot) {
        Path defaultService = projectRoot.resolve("sz-service").resolve("sz-service-admin").normalize();
        if (Files.isRegularFile(defaultService.resolve("pom.xml"))) {
            return defaultService;
        }
        Path serviceParent = projectRoot.resolve("sz-service").normalize();
        if (!Files.isDirectory(serviceParent)) {
            return defaultService;
        }
        try (var stream = Files.list(serviceParent)) {
            return stream.filter(Files::isDirectory)
                    .filter(path -> Files.isRegularFile(path.resolve("pom.xml")))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .findFirst()
                    .orElse(defaultService);
        } catch (IOException ignored) {
            return defaultService;
        }
    }

    private static List<String> resolveStartupScanPackages(Path serviceRoot) {
        Path javaRoot = serviceRoot.resolve("src/main/java");
        if (!Files.isDirectory(javaRoot)) {
            return List.of();
        }
        try (var stream = Files.walk(javaRoot)) {
            Optional<Path> application = stream.filter(path -> path.getFileName().toString().endsWith(".java")).filter(path -> {
                try {
                    return Files.readString(path, StandardCharsets.UTF_8).contains("@SpringBootApplication");
                } catch (IOException ignored) {
                    return false;
                }
            }).findFirst();
            if (application.isEmpty()) {
                return List.of();
            }
            String source = Files.readString(application.get(), StandardCharsets.UTF_8);
            List<String> configuredPackages = extractConfiguredScanPackages(source);
            if (!configuredPackages.isEmpty()) {
                return configuredPackages;
            }
            return extractJavaPackage(source).map(List::of).orElseGet(List::of);
        } catch (IOException ignored) {
            return List.of();
        }
    }

    private static List<String> extractConfiguredScanPackages(String source) {
        List<String> scanPackages = new ArrayList<>();
        Matcher springBootMatcher = SPRING_BOOT_SCAN_BASE_PACKAGES_PATTERN.matcher(source);
        while (springBootMatcher.find()) {
            addStringLiterals(scanPackages, springBootMatcher.group(1));
        }
        Matcher componentScanBaseMatcher = COMPONENT_SCAN_BASE_PACKAGES_PATTERN.matcher(source);
        while (componentScanBaseMatcher.find()) {
            addStringLiterals(scanPackages, componentScanBaseMatcher.group(1));
        }
        Matcher componentScanValueMatcher = COMPONENT_SCAN_VALUE_PATTERN.matcher(source);
        while (componentScanValueMatcher.find()) {
            addStringLiterals(scanPackages, componentScanValueMatcher.group(1));
        }
        return scanPackages.stream().distinct().toList();
    }

    private static Optional<String> extractJavaPackage(String source) {
        Matcher matcher = JAVA_PACKAGE_PATTERN.matcher(source);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    private static void addStringLiterals(List<String> values, String source) {
        Matcher matcher = STRING_LITERAL_PATTERN.matcher(source);
        while (matcher.find()) {
            String value = matcher.group(1).trim();
            if (!value.isBlank()) {
                values.add(value);
            }
        }
    }

    private static boolean isPackageCoveredByScanPackages(String packageName, List<String> scanPackages) {
        return scanPackages.stream().anyMatch(scanPackage -> packageName.equals(scanPackage) || packageName.startsWith(scanPackage + "."));
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
     *            详情
     * @param model
     *            模型
     */
    public List<MenuCreateDTO> initMenu(GeneratorDetailVO detailVO, Map<String, Object> model, boolean isInsertDB) {
        List<MenuCreateDTO> menus = new ArrayList<>();
        if ("0".equals(detailVO.getGeneratorInfo().getMenuInitType())) { // 获取代码配置，是否启用菜单（菜单不启用，按钮也不会启用）初始化
            return menus;
        }
        Long menuId = SzIdUtil.nextId(); // 按钮父级id,菜单id
        Long parentMenuId = detailVO.getGeneratorInfo().getParentMenuId();
        int menuDeep = getMenuDepth(parentMenuId);
        String routerName = model.get("indexDefineOptionsName").toString();
        String path = buildPath(detailVO);
        String component = buildComponent(detailVO);
        int count = this.mapper.selectMenuCount(parentMenuId);

        MenuCreateDTO menuDto = buildMenu(detailVO, menuId, parentMenuId, path, routerName, component, count, menuDeep);
        if (isInsertDB && menuExists(routerName, path, component, parentMenuId)) {
            return menus;
        }
        menus.add(menuDto);
        if (isInsertDB) {
            this.mapper.insertMenu(menuDto);
        }
        menus.addAll(createButtonPermissions(menuId, model, menuDeep, isInsertDB, detailVO));
        if (isInsertDB) {
            syncTreeHasChildren();
        }
        return menus;
    }

    private int getMenuDepth(Long parentMenuId) {
        SysMenuResult sysMenuResult = this.mapper.selectSysMenuByPid(parentMenuId);
        return (sysMenuResult != null) ? sysMenuResult.getDeep() + 1 : 1;
    }

    private String buildPath(GeneratorDetailVO detailVO) {
        String frontendModuleName = detailVO.getGeneratorInfo().getFrontendModuleName();
        String moduleName = frontendModuleName == null || frontendModuleName.isBlank() ? detailVO.getGeneratorInfo().getModuleName() : frontendModuleName;
        return SEPARATOR + moduleName + SEPARATOR + detailVO.getGeneratorInfo().getBusinessName(); // 获取前端设置的业务名称
    }

    private String buildComponent(GeneratorDetailVO detailVO) {
        return buildPath(detailVO) + SEPARATOR + "index";
    }

    private boolean menuExists(String routerName, String path, String component, Long parentMenuId) {
        int menuCount = this.mapper.countMenu(routerName, path, component, parentMenuId);
        String message = String.format("菜单已存在: name=%s, path=%s, component=%s, pid=%s", routerName, path, component, parentMenuId);
        boolean exists = menuCount > 0;
        if (exists) {
            log.warn(message);
        }
        return exists;
    }

    private List<MenuCreateDTO> createButtonPermissions(Long menuId, Map<String, Object> model, int menuDeep, boolean isInsertDB, GeneratorDetailVO detailVO) {
        List<MenuCreateDTO> buttonMenus = new ArrayList<>();

        int order = 1;

        if ("1".equals(detailVO.getGeneratorInfo().getBtnPermissionType())) {
            buttonMenus.add(buildAndInsertButton(menuId, "查询", model.get("listPermission").toString(), order * 100, menuDeep, isInsertDB));
            order++;
            buttonMenus.add(buildAndInsertButton(menuId, "新增", model.get("createPermission").toString(), order * 100, menuDeep, isInsertDB));
            order++;
            buttonMenus.add(buildAndInsertButton(menuId, "修改", model.get("updatePermission").toString(), order * 100, menuDeep, isInsertDB));
            order++;
            buttonMenus.add(buildAndInsertButton(menuId, "删除", model.get("removePermission").toString(), order * 100, menuDeep, isInsertDB));
            order++;
        }

        if ("1".equals(detailVO.getGeneratorInfo().getHasImport())) {
            buttonMenus.add(buildAndInsertButton(menuId, "导入", model.get("importPermission").toString(), order * 100, menuDeep, isInsertDB));
            order++;
        }

        if ("1".equals(detailVO.getGeneratorInfo().getHasExport())) {
            buttonMenus.add(buildAndInsertButton(menuId, "导出", model.get("exportPermission").toString(), order * 100, menuDeep, isInsertDB));
        }

        return buttonMenus;
    }

    private MenuCreateDTO buildAndInsertButton(Long menuId, String action, String permission, int order, int deep, boolean isInsertDB) {
        MenuCreateDTO btnDto = buildBtn(menuId, action, permission, order, deep);
        if (isInsertDB) {
            this.mapper.insertMenu(btnDto);
        }
        return btnDto;
    }

    private static MenuCreateDTO buildMenu(GeneratorDetailVO detailVO, Long btnParentId, Long parentMenuId, String path, String routerName, String component,
            int count, int parentDeep) {
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
        createDTO.setPermissions("");
        createDTO.setHasChildren("F");
        if (detailVO.getGeneratorInfo().getBtnDataScopeType().equals("1")) {
            createDTO.setUseDataScope("T");
        } else {
            createDTO.setUseDataScope("F");
        }
        return createDTO;
    }

    private MenuCreateDTO buildBtn(Long btnParentId, String btnName, String createPermission, int sort, int menuDeep) {
        MenuCreateDTO dto = new MenuCreateDTO();
        dto.setId(SzIdUtil.nextId());
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

    private void syncTreeHasChildren() {
        List<Long> menuIds = this.mapper.selectEnabledMenuIds();
        if (menuIds.isEmpty()) {
            return;
        }
        Set<Long> parentIds = new HashSet<>(this.mapper.selectEnabledMenuParentIds());
        for (Long menuId : menuIds) {
            String hasChildren = parentIds.contains(menuId) ? "T" : "F";
            this.mapper.updateMenuHasChildren(menuId, hasChildren);
        }
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

    /**
     * 是否应该初始化菜单
     * 
     * @param detailVO
     *            配置详情
     * @return boolean
     */
    private boolean shouldInitializeMenu(GeneratorDetailVO detailVO) {
        return ("1").equals(detailVO.getGeneratorInfo().getMenuInitType()) && (("all").equals(detailVO.getGeneratorInfo().getGenerateType())); // 开启菜单初始化配置 &&
                                                                                                                                               // // 代码生成类型是all
    }

    @Override
    public Template getDictSqlTemplate() throws IOException {
        return configurer.getConfiguration().getTemplate(File.separator + "sql" + File.separator + "dictImport.sql.ftl");
    }

}
