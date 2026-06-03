package com.sz.generator.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.sz.db.id.SzIdUtil;
import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.core.module.GeneratorBackendModuleScanner;
import com.sz.generator.mapper.GeneratorTableMapper;
import com.sz.generator.pojo.dto.MenuCreateDTO;
import com.sz.generator.pojo.po.GeneratorTable;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.pojo.result.SysMenuResult;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import com.sz.generator.pojo.vo.GeneratorPathOptionsVO;
import com.sz.generator.pojo.vo.GeneratorPreviewVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class GeneratorTableServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void insertDependencyManagementIfMissingShouldAddManagedModuleDependencyIdempotently() throws Exception {
        Path rootPom = tempDir.resolve("pom.xml");
        Files.writeString(rootPom, """
                <project>
                    <dependencyManagement>
                        <dependencies>
                            <dependency>
                                <groupId>com.sz</groupId>
                                <artifactId>sz-module-admin</artifactId>
                                <version>${project.version}</version>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>
                </project>
                """, StandardCharsets.UTF_8);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("insertDependencyManagementIfMissing", Path.class, String.class);
        method.setAccessible(true);
        method.invoke(null, rootPom, "sz-module-crm");
        method.invoke(null, rootPom, "sz-module-crm");

        String updated = Files.readString(rootPom, StandardCharsets.UTF_8);
        assertThat(updated).contains("""
                            <dependency>
                                <groupId>com.sz</groupId>
                                <artifactId>sz-module-crm</artifactId>
                                <version>${project.version}</version>
                            </dependency>
                """);
        assertThat(count(updated, "<artifactId>sz-module-crm</artifactId>")).isEqualTo(1);
        assertThat(updated.indexOf("<artifactId>sz-module-crm</artifactId>")).isLessThan(updated.indexOf("</dependencies>"));
    }

    @Test
    void insertIfMissingShouldKeepModulePomIndentation() throws Exception {
        Path modulePom = tempDir.resolve("pom.xml");
        Files.writeString(modulePom, """
                <project>
                    <modules>
                        <module>sz-module-admin</module>
                    </modules>
                </project>
                """, StandardCharsets.UTF_8);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("insertIfMissing", Path.class, String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(null, modulePom, "<module>sz-module-demo</module>", "        <module>sz-module-demo</module>\n", "    </modules>");
        method.invoke(null, modulePom, "<module>sz-module-demo</module>", "        <module>sz-module-demo</module>\n", "    </modules>");

        String updated = Files.readString(modulePom, StandardCharsets.UTF_8);
        assertThat(updated).contains("""
                    <modules>
                        <module>sz-module-admin</module>
                        <module>sz-module-demo</module>
                    </modules>
                """);
        assertThat(count(updated, "<module>sz-module-demo</module>")).isEqualTo(1);
    }

    @Test
    void normalizeColumnByHtmlTypeShouldConvertLegacyRadioGroupToRadio() throws Exception {
        GeneratorTableColumn column = new GeneratorTableColumn();
        column.setHtmlType("radio-group");
        column.setDictType("yes_no");

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("normalizeColumnByHtmlType", GeneratorTableColumn.class);
        method.setAccessible(true);
        method.invoke(null, column);

        assertThat(column.getHtmlType()).isEqualTo(GeneratorConstants.HTML_RADIO);
        assertThat(column.getSearchType()).isEqualTo("select");
        assertThat(column.getDictShowWay()).isEqualTo("0");
    }

    @Test
    void validateDictionaryDisplayColumnShouldRequireDictType() throws Exception {
        GeneratorTableColumn column = new GeneratorTableColumn();
        column.setColumnName("order_status");
        column.setHtmlType(GeneratorConstants.HTML_RADIO);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("validateDictionaryDisplayColumn", GeneratorTableColumn.class);
        method.setAccessible(true);

        Throwable thrown = catchThrowable(() -> method.invoke(null, column));
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).hasMessageContaining("必须配置字典类型");
    }

    @Test
    void buildModulePomShouldIncludeBusinessModuleCompileDependencies() throws Exception {
        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("buildModulePom", String.class);
        method.setAccessible(true);

        String pom = (String) method.invoke(null, "sz-module-demo");

        assertThat(pom).contains("<artifactId>sz-module-demo</artifactId>");
        assertThat(pom).contains("<artifactId>sz-common-core</artifactId>");
        assertThat(pom).contains("<artifactId>sz-common-db-core</artifactId>");
        assertThat(pom).contains("<artifactId>mybatis-flex-core</artifactId>");
        assertThat(pom).contains("<artifactId>mybatis-spring</artifactId>");
        assertThat(pom).contains("<artifactId>spring-boot-starter-web</artifactId>");
        assertThat(pom).contains("<artifactId>jakarta.validation-api</artifactId>");
        assertThat(pom).contains("<artifactId>lombok</artifactId>");
        assertThat(pom).contains("<artifactId>swagger-annotations</artifactId>");
        assertThat(pom).contains("<artifactId>sz-common-excel</artifactId>");
        assertThat(pom).contains("<artifactId>fastexcel-core</artifactId>");
        assertThat(pom).contains("<artifactId>sz-common-resource</artifactId>");
        assertThat(pom).doesNotContain("<artifactId>sz-module-admin</artifactId>");
    }

    @Test
    void moduleConfigurationsShouldUseRootPackageForApiPrefixAndMapperSubPackagesForMapperScan() throws Exception {
        Method apiMethod = GeneratorTableServiceImpl.class.getDeclaredMethod("buildApiPrefixConfiguration", String.class, String.class, String.class,
                String.class);
        apiMethod.setAccessible(true);
        Method mapperMethod = GeneratorTableServiceImpl.class.getDeclaredMethod("buildMapperScanConfiguration", String.class, String.class);
        mapperMethod.setAccessible(true);

        String apiConfig = (String) apiMethod.invoke(null, "com.sz.demo", "Demo", "demo", "/demo");
        String mapperConfig = (String) mapperMethod.invoke(null, "com.sz.demo", "Demo");

        assertThat(apiConfig).contains("return new String[]{\"com.sz.demo\"};");
        assertThat(apiConfig).doesNotContain("com.sz.demo.controller");
        assertThat(mapperConfig).contains("@MapperScan(basePackages = \"com.sz.demo.*.mapper\")");
        assertThat(mapperConfig).doesNotContain("@MapperScan(basePackages = \"com.sz.demo\")");
    }

    @Test
    void newModuleShouldGenerateExcelTemplateScanConfiguration() throws Exception {
        Method configMethod = GeneratorTableServiceImpl.class.getDeclaredMethod("buildExcelTemplateScanConfiguration", String.class, String.class);
        configMethod.setAccessible(true);

        String config = (String) configMethod.invoke(null, "com.sz.demo", "Demo");

        assertThat(config).contains("import com.sz.excel.annotation.EnableExcelTemplateScan;");
        assertThat(config).contains("import org.springframework.context.annotation.Configuration;");
        assertThat(config).contains("@Configuration");
        assertThat(config).contains("@EnableExcelTemplateScan(basePackages = \"com.sz.demo\")");
        assertThat(config).contains("public class DemoExcelTemplateScanConfiguration");
        assertThat(config).doesNotContain("@AutoConfiguration");
        assertThat(config).doesNotContain("@ComponentScan");
    }

    @Test
    @SuppressWarnings("unchecked")
    void startupScanPackagesShouldFallbackToSpringBootApplicationPackage() throws Exception {
        Path application = tempDir.resolve("src/main/java/com/sz/AdminApplication.java");
        Files.createDirectories(application.getParent());
        Files.writeString(application, """
                package com.sz;

                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                public class AdminApplication {
                }
                """, StandardCharsets.UTF_8);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("resolveStartupScanPackages", Path.class);
        method.setAccessible(true);

        List<String> scanPackages = (List<String>) method.invoke(null, tempDir);

        assertThat(scanPackages).containsExactly("com.sz");
    }

    @Test
    void packageScanCoverageShouldRejectPackagesOutsideStartupScan() throws Exception {
        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("isPackageCoveredByScanPackages", String.class, List.class);
        method.setAccessible(true);

        assertThat((Boolean) method.invoke(null, "com.sz.app", List.of("com.sz"))).isTrue();
        assertThat((Boolean) method.invoke(null, "com.sz", List.of("com.sz"))).isTrue();
        assertThat((Boolean) method.invoke(null, "com.example.app", List.of("com.sz"))).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void newModuleConflictShouldIgnoreTheSameExistingModuleTarget() throws Exception {
        Path modulePath = tempDir.resolve("sz-module").resolve("sz-module-business");
        Files.createDirectories(modulePath);
        GeneratorProperties properties = new GeneratorProperties();
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, properties, null, null, null, null);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("findNewBackendModuleConflict", Path.class, String.class, String.class, Path.class);
        method.setAccessible(true);
        Optional<String> conflict = (Optional<String>) method.invoke(service, tempDir, "sz-module-business", "business", modulePath);

        assertThat(conflict).isEmpty();
    }

    @Test
    void treeRelativePathShouldHandleRootProjectDirectory() throws Exception {
        Path root = tempDir.toAbsolutePath().getRoot();
        Path fullPath = root.resolve("sz-module").resolve("sz-module-admin").resolve("src/main/resources/db/changelog/admin/unreleased")
                .resolve("001_teacher_statistics.xml");
        String originalUserDir = System.getProperty("user.dir");
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, new GeneratorProperties(), null, null, null, null);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("toTreeRelativePath", Path.class, String.class, String.class);
        method.setAccessible(true);
        try {
            System.setProperty("user.dir", root.toString());
            String relativePath = (String) method.invoke(service, fullPath, "001_teacher_statistics.xml", fullPath.toString());

            assertThat(relativePath.replace('\\', '/'))
                    .isEqualTo("sz-boot-parent/sz-module/sz-module-admin/src/main/resources/db/changelog/admin/unreleased/001_teacher_statistics.xml");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void existingBackendModuleTargetShouldPreferScannedModulePath() throws Exception {
        Path moduleRoot = tempDir.resolve("sz-module");
        Path modulePath = moduleRoot.resolve("sz-module-admin");
        Files.createDirectories(modulePath);
        Files.writeString(moduleRoot.resolve("pom.xml"), "<project/>", StandardCharsets.UTF_8);
        Files.writeString(modulePath.resolve("pom.xml"), "<project/>", StandardCharsets.UTF_8);
        String originalUserDir = System.getProperty("user.dir");
        GeneratorProperties properties = new GeneratorProperties();
        GeneratorBackendModuleScanner scanner = new GeneratorBackendModuleScanner(properties);
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, properties, null, null, scanner, null);
        GeneratorDetailVO.GeneratorInfo info = new GeneratorDetailVO.GeneratorInfo();
        info.setBackendTargetType("existing");
        info.setBackendModuleName("sz-module-admin");
        info.setPathApi("E:\\dev\\Code\\Github\\sz-boot-parent\\sz-service\\sz-service-admin");

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("resolveBackendModuleTarget", GeneratorDetailVO.GeneratorInfo.class);
        method.setAccessible(true);
        try {
            System.setProperty("user.dir", tempDir.toString());
            Object target = method.invoke(service, info);
            Method modulePathMethod = target.getClass().getDeclaredMethod("modulePath");
            modulePathMethod.setAccessible(true);

            assertThat((Path) modulePathMethod.invoke(target)).isEqualTo(modulePath.normalize());
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void pathOptionsShouldReturnDefaultAdminModuleWhenSourceTreeIsUnavailable() {
        String originalUserDir = System.getProperty("user.dir");
        GeneratorProperties properties = new GeneratorProperties();
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, properties, null, null, new GeneratorBackendModuleScanner(properties),
                null);

        try {
            System.setProperty("user.dir", tempDir.toString());
            GeneratorPathOptionsVO options = service.pathOptions();

            assertThat(options.getBackendModuleOptions()).hasSize(1);
            assertThat(options.getBackendModuleOptions().getFirst().getModuleName()).isEqualTo("sz-module-admin");
            assertThat(options.getBackendModuleOptions().getFirst().getApiPrefixModule()).isEqualTo("admin");
            assertThat(options.getDefaultApiPath()).contains("sz-module-admin");
            assertThat(options.getDefaultWebPath()).contains("sz-admin");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void defaultModuleInfoShouldBackfillImportedTableWhenSourceTreeIsUnavailable() throws Exception {
        String originalUserDir = System.getProperty("user.dir");
        GeneratorProperties properties = new GeneratorProperties();
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, properties, null, null, new GeneratorBackendModuleScanner(properties),
                null);
        GeneratorTable table = new GeneratorTable();

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("applyDefaultModuleInfo", GeneratorTable.class);
        method.setAccessible(true);
        try {
            System.setProperty("user.dir", tempDir.toString());
            method.invoke(service, table);

            assertThat(table.getBackendTargetType()).isEqualTo("existing");
            assertThat(table.getBackendModuleName()).isEqualTo("sz-module-admin");
            assertThat(table.getApiPrefixModule()).isEqualTo("admin");
            assertThat(table.getApiPrefix()).isEqualTo("/admin");
            assertThat(table.getFrontendModuleName()).isEqualTo("admin");
            assertThat(table.getPathApi()).contains("sz-module-admin");
            assertThat(table.getPathWeb()).contains("sz-admin");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void businessTableChangelogShouldCreateTableOnlyWhenMissing() throws Exception {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.BaseInfo baseInfo = new GeneratorDetailVO.BaseInfo();
        baseInfo.setTableName("test_gen_work_order");
        baseInfo.setTableComment("代码生成器测试工单表");
        detailVO.setBaseInfo(baseInfo);

        GeneratorDetailVO.Column idColumn = new GeneratorDetailVO.Column();
        idColumn.setColumnName("id");
        idColumn.setColumnType("bigint");
        idColumn.setColumnComment("主键ID");
        idColumn.setIsPk("1");
        idColumn.setIsIncrement("1");

        GeneratorDetailVO.Column orderNoColumn = new GeneratorDetailVO.Column();
        orderNoColumn.setColumnName("order_no");
        orderNoColumn.setColumnType("varchar(64)");
        orderNoColumn.setColumnComment("工单编号");
        orderNoColumn.setIsRequired("1");
        orderNoColumn.setIsUniqueValid("1");

        GeneratorDetailVO.Column expectTimeColumn = new GeneratorDetailVO.Column();
        expectTimeColumn.setColumnName("expect_time");
        expectTimeColumn.setColumnType("datetime");
        expectTimeColumn.setColumnComment("期望处理时间");

        GeneratorDetailVO.Column extendColumn = new GeneratorDetailVO.Column();
        extendColumn.setColumnName("extend_json");
        extendColumn.setColumnType("json");
        extendColumn.setColumnComment("扩展JSON");

        detailVO.setColumns(List.of(idColumn, orderNoColumn, expectTimeColumn, extendColumn));

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("buildBusinessTableChangelog", GeneratorDetailVO.class, String.class);
        method.setAccessible(true);
        String changelog = (String) method.invoke(null, detailVO, "business");

        assertThat(changelog).contains("<preConditions onFail=\"MARK_RAN\">");
        assertThat(changelog).contains("<not><tableExists tableName=\"test_gen_work_order\"/></not>");
        assertThat(changelog).contains("<createTable tableName=\"test_gen_work_order\" remarks=\"代码生成器测试工单表\">");
        assertThat(changelog).contains("<column name=\"id\" type=\"BIGINT\" autoIncrement=\"true\" remarks=\"主键ID\"><constraints primaryKey=\"true\"");
        assertThat(changelog).contains("<column name=\"expect_time\" type=\"${datetime.type}\"");
        assertThat(changelog).contains("<column name=\"extend_json\" type=\"${json.type}\"");
        assertThat(changelog).contains("<createIndex tableName=\"test_gen_work_order\" indexName=\"uk_test_gen_work_order_order_no\" unique=\"true\">");
    }

    @Test
    void subChangelogMasterShouldIncludeBusinessTableWithoutEmptyInitFile() throws Exception {
        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("buildSubChangelogMaster", List.class);
        method.setAccessible(true);

        String master = (String) method.invoke(null, List.of("<include file=\"unreleased/001_test_gen_work_order.xml\" relativeToChangelogFile=\"true\"/>"));

        assertThat(master).contains("<include file=\"unreleased/001_test_gen_work_order.xml\" relativeToChangelogFile=\"true\"/>");
        assertThat(master).doesNotContain("_init.xml");
    }

    @Test
    void businessTableIncludeShouldBeRelativeToSubChangelogMaster() throws Exception {
        Path modulePath = tempDir.resolve("sz-module-app");
        Path tableChangelog = modulePath.resolve("src/main/resources/db/changelog/app/unreleased/001_test_gen_work_order.xml");
        Object target = newBackendModuleTarget("sz-module-app", "app", modulePath, "unreleased");

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("masterTableInclude", target.getClass(), Path.class);
        method.setAccessible(true);
        String include = (String) method.invoke(null, target, tableChangelog);

        assertThat(include).isEqualTo("<include file=\"unreleased/001_test_gen_work_order.xml\" relativeToChangelogFile=\"true\"/>");
    }

    @Test
    void businessTableChangelogFileNameShouldUseNextSequenceAndTableName() throws Exception {
        Path modulePath = tempDir.resolve("sz-module-business");
        Path unreleased = modulePath.resolve("src/main/resources/db/changelog/business/unreleased");
        Files.createDirectories(unreleased);
        Files.writeString(unreleased.resolve("001_old_table.xml"), "", StandardCharsets.UTF_8);
        Object target = newBackendModuleTarget("sz-module-business", "business", modulePath, "unreleased");

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("nextBusinessTableChangelogFileName", target.getClass(), String.class);
        method.setAccessible(true);
        String fileName = (String) method.invoke(null, target, "test_gen_business_order");

        assertThat(fileName).isEqualTo("002_test_gen_business_order.xml");
    }

    @Test
    void businessTableChangelogFileNameShouldReuseExistingTableFile() throws Exception {
        Path modulePath = tempDir.resolve("sz-module-business");
        Path unreleased = modulePath.resolve("src/main/resources/db/changelog/business/unreleased");
        Files.createDirectories(unreleased);
        Files.writeString(unreleased.resolve("003_test_gen_business_order.xml"), "", StandardCharsets.UTF_8);
        Object target = newBackendModuleTarget("sz-module-business", "business", modulePath, "unreleased");

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("nextBusinessTableChangelogFileName", target.getClass(), String.class);
        method.setAccessible(true);
        String fileName = (String) method.invoke(null, target, "test_gen_business_order");

        assertThat(fileName).isEqualTo("003_test_gen_business_order.xml");
    }

    @Test
    @SuppressWarnings("unchecked")
    void modificationGuideShouldDescribeModifyItemsForZipUsers() throws Exception {
        GeneratorPreviewVO modifyItem = new GeneratorPreviewVO();
        modifyItem.setOperationType("MODIFY_FILE");
        modifyItem.setRelativePath("sz-boot-parent/sz-module/pom.xml");
        modifyItem.setMessage("接入模块聚合 POM。");
        modifyItem.setDiff("""
                --- a/sz-boot-parent/sz-module/pom.xml
                +++ b/sz-boot-parent/sz-module/pom.xml
                @@ before </modules> @@
                +        <module>sz-module-business</module>
                """);

        Method method = GeneratorTableServiceImpl.class.getDeclaredMethod("buildModificationGuidePreviews", List.class);
        method.setAccessible(true);
        List<GeneratorPreviewVO> guides = (List<GeneratorPreviewVO>) method.invoke(null, List.of(modifyItem));

        assertThat(guides).hasSize(1);
        GeneratorPreviewVO guide = guides.getFirst();
        assertThat(guide.getName()).isEqualTo("修改说明.txt");
        assertThat(guide.getOperationType()).isEqualTo("SCRIPT");
        assertThat(guide.getContent()).contains("代码生成修改说明");
        assertThat(guide.getContent()).contains("sz-boot-parent/sz-module/pom.xml");
        assertThat(guide.getContent()).contains("<module>sz-module-business</module>");
    }

    @Test
    void initMenuShouldInsertRootMenuAndButtonsWhenMenuDoesNotExist() throws Exception {
        RecordingGeneratorTableMapper mapper = new RecordingGeneratorTableMapper(0);
        GeneratorTableServiceImpl service = serviceWithMapper(mapper.proxy());

        List<MenuCreateDTO> menus = service.initMenu(generatorDetail(), generatorModel(), true);

        assertThat(menus).hasSize(7);
        assertThat(mapper.insertedMenus).hasSize(7);
        assertThat(mapper.insertedMenus.get(0).getMenuTypeCd()).isEqualTo("1002002");
        assertThat(mapper.insertedMenus.subList(1, mapper.insertedMenus.size())).allMatch(menu -> "1002003".equals(menu.getMenuTypeCd()));
    }

    @Test
    void initMenuShouldSkipRootMenuAndButtonsWhenMenuAlreadyExists() throws Exception {
        RecordingGeneratorTableMapper mapper = new RecordingGeneratorTableMapper(1);
        GeneratorTableServiceImpl service = serviceWithMapper(mapper.proxy());

        List<MenuCreateDTO> menus = service.initMenu(generatorDetail(), generatorModel(), true);

        assertThat(menus).isEmpty();
        assertThat(mapper.insertedMenus).isEmpty();
    }

    private static Object newBackendModuleTarget(String moduleName, String moduleCode, Path modulePath, String changelogDirectory) throws Exception {
        Class<?> targetClass = Class.forName("com.sz.generator.service.impl.GeneratorTableServiceImpl$BackendModuleTarget");
        var constructor = targetClass.getDeclaredConstructor(String.class, String.class, Path.class, String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(moduleName, moduleCode, modulePath, changelogDirectory);
    }

    private static GeneratorTableServiceImpl serviceWithMapper(GeneratorTableMapper mapper) throws Exception {
        new SzIdUtil(new Snowflake(1, 1));
        GeneratorTableServiceImpl service = new GeneratorTableServiceImpl(null, null, new GeneratorProperties(), null, null, null, null);
        Field mapperField = findField(service.getClass(), "mapper");
        mapperField.setAccessible(true);
        mapperField.set(service, mapper);
        return service;
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static GeneratorDetailVO generatorDetail() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.GeneratorInfo info = new GeneratorDetailVO.GeneratorInfo();
        info.setModuleName("admin");
        info.setFrontendModuleName("admin");
        info.setBusinessName("testGenBusinessOrder");
        info.setFunctionName("测试业务订单");
        info.setParentMenuId(0L);
        info.setMenuInitType("1");
        info.setBtnPermissionType("1");
        info.setHasImport("1");
        info.setHasExport("1");
        info.setBtnDataScopeType("0");
        detailVO.setGeneratorInfo(info);
        return detailVO;
    }

    private static Map<String, Object> generatorModel() {
        return Map.of("indexDefineOptionsName", "TestGenBusinessOrderView", "listPermission", "test.gen.business.order.query", "createPermission",
                "test.gen.business.order.create", "updatePermission", "test.gen.business.order.update", "removePermission", "test.gen.business.order.remove",
                "importPermission", "test.gen.business.order.import", "exportPermission", "test.gen.business.order.export");
    }

    private static class RecordingGeneratorTableMapper implements InvocationHandler {

        private final int menuCount;

        private final List<MenuCreateDTO> insertedMenus = new ArrayList<>();

        private RecordingGeneratorTableMapper(int menuCount) {
            this.menuCount = menuCount;
        }

        private GeneratorTableMapper proxy() {
            return (GeneratorTableMapper) Proxy.newProxyInstance(GeneratorTableMapper.class.getClassLoader(), new Class<?>[]{GeneratorTableMapper.class}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "countMenu" -> menuCount;
                case "insertMenu" -> {
                    insertedMenus.add((MenuCreateDTO) args[0]);
                    yield null;
                }
                case "selectSysMenuByPid" -> {
                    SysMenuResult result = new SysMenuResult();
                    result.setDeep(0);
                    yield result;
                }
                case "selectMenuCount" -> 0;
                case "selectEnabledMenuIds", "selectEnabledMenuParentIds" -> List.of();
                case "toString" -> "RecordingGeneratorTableMapper";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == double.class) {
            return 0D;
        }
        if (returnType == float.class) {
            return 0F;
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == char.class) {
            return (char) 0;
        }
        return null;
    }

    private static int count(String text, String token) {
        int count = 0;
        int index = text.indexOf(token);
        while (index >= 0) {
            count++;
            index = text.indexOf(token, index + token.length());
        }
        return count;
    }
}
