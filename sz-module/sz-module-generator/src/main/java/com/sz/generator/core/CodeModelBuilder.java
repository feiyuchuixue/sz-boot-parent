package com.sz.generator.core;

import com.sz.core.util.DateUtils;
import com.sz.core.util.Utils;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import lombok.Getter;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author sz
 * @since 2024/1/15 15:10
 */
@Getter
public class CodeModelBuilder {

    private final Map<String, Object> model = new HashMap<>();

    // vue 文件夹路径分隔符
    public static final String SEPARATOR = "/";

    public CodeModelBuilder builderBaseInfo(GeneratorDetailVO detailVO) {
        String className = detailVO.getBaseInfo().getClassName();
        String packageName = detailVO.getGeneratorInfo().getPackageName();
        String router = getRouter(detailVO); // 路由名
        String tableName = detailVO.getBaseInfo().getTableName(); // 表名，例如sys_user
        String functionName = detailVO.getGeneratorInfo().getFunctionName(); // 方法名或业务名，例如：教师统计
        String businessName = detailVO.getGeneratorInfo().getBusinessName();

        List<GeneratorDetailVO.Column> columns = detailVO.getColumns();
        model.put("packageName", packageName);
        model.put("tableComment", detailVO.getBaseInfo().getTableComment());
        model.put("author", detailVO.getBaseInfo().getFunctionAuthor());
        model.put("datetime", DateUtils.getDefaultDate());
        model.put("tableName", tableName);
        model.put("className", className);
        model.put("columns", columns);
        model.put("camelClassName", detailVO.getBaseInfo().getCamelClassName());
        model.put("functionName", functionName);
        model.put("businessName", businessName);
        model.put("GeneratorInfo", detailVO.getGeneratorInfo());
        model.put("router", router);
        model.put("dictTypes", detailVO.getDictTypes());

        return this;
    }

    public CodeModelBuilder builderImportPackage(GeneratorDetailVO detailVO) {
        List<GeneratorDetailVO.Column> columns = detailVO.getColumns();
        Set<String> importPackages = new TreeSet<>();
        boolean hasUniqueValidField = false;
        boolean hasDateFormat = false;
        for (GeneratorDetailVO.Column column : columns) {
            String javaTypePackage = column.getJavaTypePackage();
            if (Utils.isNotNull(javaTypePackage)) {
                String[] split = javaTypePackage.split(",");
                Arrays.stream(split)
                        .map(String::trim)
                        .filter(item -> !item.isBlank())
                        .forEach(importPackages::add);
                // importPackages.add(javaTypePackage);
            }
            if (("LocalDateTime").equals(column.getJavaType())) {
                hasDateFormat = true;
            }
            if (("1").equals(column.getIsUniqueValid())) {
                hasUniqueValidField = true;
            }
            if (column.getJavaType().startsWith("List")) {
                importPackages.add("java.util.List");
            }
            if (GeneratorConstants.TYPE_LIST_UPLOADRESULT.equals(column.getJavaType())) {
                importPackages.add("com.mybatisflex.annotation.Column");
                importPackages.add("com.sz.db.handler.Jackson3TypeHandler");
                importPackages.add("com.sz.resource.model.ResourceRef");
            }
        }
        model.put("importPackages", importPackages);
        model.put("hasUniqueValidField", hasUniqueValidField);
        model.put("hasDateFormat", hasDateFormat);
        return this;
    }

    public CodeModelBuilder builderDynamicsParam(GeneratorDetailVO detailVO) {
        String idType = "";
        String idJavaType = "Long";
        String pkName = "";
        boolean hasDict = false;
        boolean hasSelect = false;
        boolean hasExcel = false;
        boolean hasResourceRef = false;
        GeneratorDetailVO.Column importBizKeyColumn = null;
        List<GeneratorDetailVO.Column> pkColumns = new ArrayList<>();
        List<GeneratorDetailVO.Column> columns = detailVO.getColumns();

        boolean hasGenExcel = ("1").equals(detailVO.getGeneratorInfo().getHasImport()) || ("1").equals(detailVO.getGeneratorInfo().getHasExport());

        for (GeneratorDetailVO.Column column : columns) {
            if (hasGenExcel && (("1").equals(column.getIsImport()) || ("1").equals(column.getIsExport()))) {
                hasExcel = true;
            }

            if (("1").equals(column.getIsPk())) {
                idType = column.getTsType();
                idJavaType = defaultString(column.getJavaType(), idJavaType);
                pkName = column.getJavaField();
            }
            if (Utils.isNotNull(column.getDictType())) {
                hasDict = true;
            }
            if (("select").equals(column.getHtmlType()) || ("radio").equals(column.getHtmlType()) || ("radio-group").equals(column.getHtmlType())
                    || ("checkbox").equals(column.getHtmlType())) {
                hasSelect = true;
            }
            if (column.getIsPk().equals("1")) {
                pkColumns.add(column);
            }
            // 检测是否存在 List<ResourceRef> 类型字段
            if (GeneratorConstants.TYPE_LIST_UPLOADRESULT.equals(column.getJavaType())) {
                hasResourceRef = true;
            }
            if (("1").equals(column.getIsImport()) && shouldUseImportBizKey(importBizKeyColumn, column)) {
                importBizKeyColumn = column;
            }
        }
        model.put("pkName", pkName);
        model.put("idJavaType", idJavaType);
        model.put("hasDict", hasDict);
        model.put("hasSelect", hasSelect);
        model.put("hasExcel", hasExcel);
        model.put("idType", idType);
        model.put("pkColumns", pkColumns);
        model.put("hasResourceRef", hasResourceRef);
        model.put("importBizKeyColumn", importBizKeyColumn);
        return this;
    }

    private static boolean shouldUseImportBizKey(GeneratorDetailVO.Column current, GeneratorDetailVO.Column candidate) {
        if (current == null) {
            return true;
        }
        if (("1").equals(candidate.getIsUniqueValid()) && !("1").equals(current.getIsUniqueValid())) {
            return true;
        }
        return !("1").equals(current.getIsUniqueValid()) && ("1").equals(candidate.getIsRequired()) && !("1").equals(current.getIsRequired());
    }

    public CodeModelBuilder builderPojo(GeneratorDetailVO detailVO) {
        String packageGroup = detailVO.getGeneratorInfo().getModuleName();
        String poPkg = buildPackagePath(detailVO, packageGroup, "pojo" + File.separator + "po");
        String mapperPkg = buildPackagePath(detailVO, packageGroup, "mapper");
        String servicePkg = buildPackagePath(detailVO, packageGroup, "service");
        String serviceImplPkg = buildPackagePath(detailVO, packageGroup, "service" + File.separator + "impl");
        String controllerPkg = buildPackagePath(detailVO, packageGroup, "controller");
        String mapperXmlPkg = Paths.get("mapper", packageGroup).toString();
        String dtoPkg = buildPackagePath(detailVO, packageGroup, "pojo" + File.separator + "dto");
        String voPkg = buildPackagePath(detailVO, packageGroup, "pojo" + File.separator + "vo");

        model.put("poPkg", poPkg);
        model.put("mapperPkg", mapperPkg);
        model.put("servicePkg", servicePkg);
        model.put("serviceImplPkg", serviceImplPkg);
        model.put("controllerPkg", controllerPkg);
        model.put("mapperXmlPkg", mapperXmlPkg);
        model.put("dtoPkg", dtoPkg);
        model.put("voPkg", voPkg);

        model.put("poClassName", detailVO.getBaseInfo().getClassName());
        model.put("mapperClassName", detailVO.getBaseInfo().getClassName() + "Mapper");
        model.put("serviceClassName", detailVO.getBaseInfo().getClassName() + "Service");
        model.put("serviceImplClassName", detailVO.getBaseInfo().getClassName() + "ServiceImpl");
        model.put("controllerClassName", detailVO.getBaseInfo().getClassName() + "Controller");
        model.put("dtoCreateClassName", detailVO.getBaseInfo().getClassName() + "CreateDTO");
        model.put("dtoUpdateClassName", detailVO.getBaseInfo().getClassName() + "UpdateDTO");
        model.put("dtoListClassName", detailVO.getBaseInfo().getClassName() + "ListDTO");
        model.put("dtoImportClassName", detailVO.getBaseInfo().getClassName() + "ImportDTO");
        model.put("voClassName", detailVO.getBaseInfo().getClassName() + "VO");

        // ExcelImporter 相关
        model.put("excelImporterClassName", detailVO.getBaseInfo().getClassName() + "ExcelImporter");
        String excelImporterPkg = buildPackagePath(detailVO, packageGroup, "service" + File.separator + "support");
        model.put("excelImporterPkg", excelImporterPkg);

        return this;
    }

    public CodeModelBuilder builderVue(GeneratorDetailVO detailVO) {
        String className = detailVO.getBaseInfo().getClassName();
        GeneratorDetailVO.GeneratorInfo generatorInfo = detailVO.getGeneratorInfo();
        String frontendLayout = defaultString(generatorInfo.getFrontendLayout(), "module");
        String frontendModuleName = resolveFrontendModuleName(generatorInfo, frontendLayout);
        String frontendModuleVarName = toModuleVarName(frontendModuleName);
        String apiPrefixModule = defaultString(generatorInfo.getApiPrefixModule(), "admin");
        String apiPrefix = defaultString(generatorInfo.getApiPrefix(), "/" + apiPrefixModule);
        String httpClientName = apiPrefixModule + "Http";
        boolean builtinHttpClient = Set.of("admin", "audit", "generator").contains(apiPrefixModule);
        String interfacePkg = SEPARATOR + "api" + SEPARATOR + "interface" + SEPARATOR + generatorInfo.getModuleName();
        model.put("interfacePkg", interfacePkg);
        model.put("interfaceClassName", generatorInfo.getBusinessName());
        model.put("interfaceNamespace", detailVO.getBaseInfo().getClassName());

        String typePkg = "legacy".equals(frontendLayout) ? SEPARATOR + "api" + SEPARATOR + "types" + SEPARATOR + generatorInfo.getModuleName()
                : SEPARATOR + "modules" + SEPARATOR + frontendModuleName + SEPARATOR + "types";
        model.put("typePkg", typePkg);
        model.put("typeClassName", generatorInfo.getBusinessName());

        String modulesPkg = "legacy".equals(frontendLayout) ? SEPARATOR + "api" + SEPARATOR + "modules" + SEPARATOR + generatorInfo.getModuleName()
                : SEPARATOR + "modules" + SEPARATOR + frontendModuleName + SEPARATOR + "api";
        model.put("modulesPkg", modulesPkg);
        model.put("modulesClassName", generatorInfo.getBusinessName());

        String indexPkg = "legacy".equals(frontendLayout) ? SEPARATOR + "views" + SEPARATOR + generatorInfo.getModuleName() + SEPARATOR
                + generatorInfo.getBusinessName()
                : SEPARATOR + "modules" + SEPARATOR + frontendModuleName + SEPARATOR + "views" + SEPARATOR + generatorInfo.getBusinessName();
        model.put("indexPkg", indexPkg);
        model.put("indexClassName", "index");

        String formPkg = indexPkg + SEPARATOR + "components";
        model.put("formPkg", formPkg);
        model.put("formClassName", detailVO.getBaseInfo().getClassName() + "Form");

        model.put("funGetList", "get" + className + "ListApi");
        model.put("funCreate", "create" + className + "Api");
        model.put("funUpdate", "update" + className + "Api");
        model.put("funDetail", "get" + className + "DetailApi");
        model.put("funRemove", "remove" + className + "Api");
        model.put("funImport", "import" + className + "ExcelApi");
        model.put("funExport", "export" + className + "ExcelApi");
        model.put("frontendLayout", frontendLayout);
        model.put("frontendModuleName", frontendModuleName);
        model.put("frontendModuleVarName", frontendModuleVarName);
        model.put("registerPkg", SEPARATOR + "modules" + SEPARATOR + frontendModuleName);
        model.put("registerClassName", "register");
        model.put("registeredComponent", SEPARATOR + frontendModuleName + SEPARATOR + generatorInfo.getBusinessName() + SEPARATOR + "index");
        model.put("httpClientName", builtinHttpClient ? httpClientName : "moduleHttp");
        model.put("httpClientImportName", builtinHttpClient ? httpClientName : "createModuleHttp");
        model.put("builtinHttpClient", builtinHttpClient);
        model.put("apiPrefixModule", apiPrefixModule);
        model.put("apiPrefix", apiPrefix);

        // permission标识
        String permissionHeader = getRouter(detailVO).replace("-", ".");
        String createPermission = permissionHeader + ".create";
        String updatePermission = permissionHeader + ".update";
        String removePermission = permissionHeader + ".remove";
        String importPermission = permissionHeader + ".import";
        String exportPermission = permissionHeader + ".export";
        String listPermission = permissionHeader + ".query_table";

        model.put("createPermission", createPermission);
        model.put("updatePermission", updatePermission);
        model.put("removePermission", removePermission);
        model.put("importPermission", importPermission);
        model.put("exportPermission", exportPermission);
        model.put("listPermission", listPermission);
        model.put("indexDefineOptionsName", className + "View"); // vue index DefineOptionsName
        return this;
    }

    private static String defaultString(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String resolveFrontendModuleName(GeneratorDetailVO.GeneratorInfo generatorInfo, String frontendLayout) {
        if (!"legacy".equals(frontendLayout)) {
            String frontendModuleName = generatorInfo.getFrontendModuleName();
            if (frontendModuleName != null && !frontendModuleName.isBlank()) {
                return normalizeFrontendModuleCode(frontendModuleName);
            }
            String moduleCode = normalizeFrontendModuleCode(generatorInfo.getBackendModuleName());
            if (!moduleCode.isBlank()) {
                return moduleCode;
            }
        }
        return defaultString(generatorInfo.getFrontendModuleName(), generatorInfo.getModuleName());
    }

    private static String normalizeFrontendModuleCode(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().replaceFirst("^sz-module-", "").replace("_", "-").toLowerCase(Locale.ROOT);
    }

    private static String toModuleVarName(String moduleName) {
        String normalized = moduleName == null ? "" : moduleName.trim();
        StringBuilder builder = new StringBuilder();
        boolean upperNext = false;
        for (char c : normalized.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                upperNext = builder.length() > 0;
                continue;
            }
            if (builder.isEmpty()) {
                if (Character.isDigit(c)) {
                    builder.append("module");
                    builder.append(c);
                } else {
                    builder.append(Character.toLowerCase(c));
                }
            } else {
                builder.append(upperNext ? Character.toUpperCase(c) : c);
            }
            upperNext = false;
        }
        if (builder.isEmpty()) {
            builder.append("demo");
        }
        builder.append("Module");
        return builder.toString();
    }

    private static String buildPackagePath(GeneratorDetailVO detailVO, String packageGroup, String subPackage) {
        String basePackage = detailVO.getGeneratorInfo().getPackageName();
        return Paths.get(basePackage, packageGroup, subPackage).toString().replace(File.separator, ".");

    }

    private static String getRouter(GeneratorDetailVO detailVO) {
        return detailVO.getBaseInfo().getTableName().replace("_", "-");
    }

}
