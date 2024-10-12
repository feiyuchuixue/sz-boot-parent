package com.sz.generator.core;

import com.sz.core.util.DateUtils;
import com.sz.core.util.Utils;
import com.sz.generator.pojo.vo.GeneratorDetailVO;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * @ClassName CodeModelBuilder
 * @Author sz
 * @Date 2024/1/15 15:10
 * @Version 1.0
 */
public class CodeModelBuilder {

    private Map<String, Object> model = new HashMap<>();

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
                importPackages.add(javaTypePackage);
            }
            if (("LocalDateTime").equals(column.getJavaType())) {
                hasDateFormat = true;
            }
            if (("1").equals(column.getIsUniqueValid())) {
                hasUniqueValidField = true;
            }
        }
        /*
         * if (hasDateFormat) {
         * importPackages.add("org.springframework.format.annotation.DateTimeFormat"); }
         */
        model.put("importPackages", importPackages);
        model.put("hasUniqueValidField", hasUniqueValidField);
        model.put("hasDateFormat", hasDateFormat);
        return this;
    }

    public CodeModelBuilder builderDynamicsParam(GeneratorDetailVO detailVO) {
        String idType = "";
        String pkName = "";
        boolean hasDict = false;
        boolean hasSelect = false;
        boolean hasExcel = false;
        List<GeneratorDetailVO.Column> pkColumns = new ArrayList<>();
        List<GeneratorDetailVO.Column> columns = detailVO.getColumns();

        boolean hasGenExcel = ("1").equals(detailVO.getGeneratorInfo().getHasImport()) || ("1").equals(detailVO.getGeneratorInfo().getHasExport());

        for (GeneratorDetailVO.Column column : columns) {
            if (hasGenExcel && (("1").equals(column.getIsImport()) || ("1").equals(column.getIsExport()))) {
                hasExcel = true;
            }

            if (("1").equals(column.getIsPk())) {
                idType = column.getTsType();
                pkName = column.getJavaField();
            }
            if (Utils.isNotNull(column.getDictType())) {
                hasDict = true;
            }
            if (("select").equals(column.getHtmlType()) || ("radio").equals(column.getHtmlType())) {
                hasSelect = true;
            }
            if (column.getIsPk().equals("1")) {
                pkColumns.add(column);
            }
        }
        model.put("pkName", pkName);
        model.put("hasDict", hasDict);
        model.put("hasSelect", hasSelect);
        model.put("hasExcel", hasExcel);
        model.put("idType", idType);
        model.put("pkColumns", pkColumns);
        return this;
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

        return this;
    }

    public CodeModelBuilder builderVue(GeneratorDetailVO detailVO) {
        String className = detailVO.getBaseInfo().getClassName();
        String interfacePkg = SEPARATOR + "api" + SEPARATOR + "interface" + SEPARATOR + detailVO.getGeneratorInfo().getModuleName();
        model.put("interfacePkg", interfacePkg);
        model.put("interfaceClassName", detailVO.getGeneratorInfo().getBusinessName());
        model.put("interfaceNamespace", "I" + detailVO.getBaseInfo().getClassName());

        String modulesPkg = SEPARATOR + "api" + SEPARATOR + "modules" + SEPARATOR + detailVO.getGeneratorInfo().getModuleName();
        model.put("modulesPkg", modulesPkg);
        model.put("modulesClassName", detailVO.getGeneratorInfo().getBusinessName());

        String indexPkg = SEPARATOR + "views" + SEPARATOR + detailVO.getGeneratorInfo().getModuleName() + SEPARATOR
                + detailVO.getGeneratorInfo().getBusinessName();
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

    public Map<String, Object> getModel() {
        return this.model;
    }

    private static String buildPackagePath(GeneratorDetailVO detailVO, String packageGroup, String subPackage) {
        String basePackage = detailVO.getGeneratorInfo().getPackageName();
        return Paths.get(basePackage, packageGroup, subPackage).toString().replace(File.separator, ".");

    }

    private static String getRouter(GeneratorDetailVO detailVO) {
        return detailVO.getBaseInfo().getTableName().replace("_", "-");
    }

}
