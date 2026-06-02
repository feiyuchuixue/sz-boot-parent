package com.sz.generator.core;

import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CodeModelBuilderTest {

    @Test
    void builderVueShouldRespectConfiguredFrontendModuleForModuleLayout() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.BaseInfo baseInfo = new GeneratorDetailVO.BaseInfo();
        baseInfo.setTableName("sys_role_menu");
        baseInfo.setTableComment("System role menu");
        baseInfo.setClassName("SysRoleMenu");
        baseInfo.setCamelClassName("sysRoleMenu");
        detailVO.setBaseInfo(baseInfo);

        GeneratorDetailVO.GeneratorInfo generatorInfo = new GeneratorDetailVO.GeneratorInfo();
        generatorInfo.setPackageName("com.sz.audit");
        generatorInfo.setModuleName("system");
        generatorInfo.setBusinessName("sysRoleMenu");
        generatorInfo.setFunctionName("System role menu");
        generatorInfo.setFrontendLayout("module");
        generatorInfo.setFrontendModuleName("admin");
        generatorInfo.setBackendModuleName("sz-module-audit");
        generatorInfo.setApiPrefixModule("audit");
        generatorInfo.setApiPrefix("/audit");
        detailVO.setGeneratorInfo(generatorInfo);
        detailVO.setColumns(List.of());
        detailVO.setDictTypes(Set.of());

        Map<String, Object> model = new CodeModelBuilder()
                .builderBaseInfo(detailVO)
                .builderVue(detailVO)
                .getModel();

        assertThat(model.get("frontendModuleName")).isEqualTo("admin");
        assertThat(model.get("frontendModuleVarName")).isEqualTo("adminModule");
        assertThat(model.get("modulesPkg")).isEqualTo("/modules/admin/api");
        assertThat(model.get("typePkg")).isEqualTo("/modules/admin/types");
        assertThat(model.get("indexPkg")).isEqualTo("/modules/admin/views/sysRoleMenu");
        assertThat(model.get("registerPkg")).isEqualTo("/modules/admin");
        assertThat(model.get("registeredComponent")).isEqualTo("/admin/sysRoleMenu/index");
        assertThat(model.get("httpClientName")).isEqualTo("auditHttp");
    }

    @Test
    void builderVueShouldCreateCustomHttpClientForNewNonBuiltinModule() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.BaseInfo baseInfo = new GeneratorDetailVO.BaseInfo();
        baseInfo.setTableName("crm_order");
        baseInfo.setTableComment("CRM order");
        baseInfo.setClassName("CrmOrder");
        baseInfo.setCamelClassName("crmOrder");
        detailVO.setBaseInfo(baseInfo);

        GeneratorDetailVO.GeneratorInfo generatorInfo = new GeneratorDetailVO.GeneratorInfo();
        generatorInfo.setPackageName("com.sz.crm");
        generatorInfo.setModuleName("order");
        generatorInfo.setBusinessName("crmOrder");
        generatorInfo.setFunctionName("CRM order");
        generatorInfo.setFrontendLayout("module");
        generatorInfo.setBackendModuleName("sz-module-crm");
        generatorInfo.setApiPrefixModule("crm");
        generatorInfo.setApiPrefix("/crm-api");
        detailVO.setGeneratorInfo(generatorInfo);
        detailVO.setColumns(List.of());
        detailVO.setDictTypes(Set.of());

        Map<String, Object> model = new CodeModelBuilder()
                .builderBaseInfo(detailVO)
                .builderVue(detailVO)
                .getModel();

        assertThat(model.get("frontendModuleName")).isEqualTo("crm");
        assertThat(model.get("frontendModuleVarName")).isEqualTo("crmModule");
        assertThat(model.get("httpClientName")).isEqualTo("moduleHttp");
        assertThat(model.get("httpClientImportName")).isEqualTo("createModuleHttp");
        assertThat(model.get("apiPrefixModule")).isEqualTo("crm");
        assertThat(model.get("apiPrefix")).isEqualTo("/crm-api");
    }

    @Test
    @SuppressWarnings("unchecked")
    void builderImportPackageShouldAddUploadFieldImportsWhenJavaTypeIsResourceRefList() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.Column column = new GeneratorDetailVO.Column();
        column.setJavaType(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
        column.setJavaTypePackage("");
        detailVO.setColumns(List.of(column));

        Map<String, Object> model = new CodeModelBuilder()
                .builderImportPackage(detailVO)
                .getModel();

        assertThat((Set<String>) model.get("importPackages"))
                .contains(
                        "java.util.List",
                        "com.sz.resource.model.ResourceRef",
                        "com.sz.db.handler.Jackson3TypeHandler",
                        "com.mybatisflex.annotation.Column"
                );
    }

    @Test
    void builderDynamicsParamShouldUseImportedBusinessFieldAsImportFailKey() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.GeneratorInfo generatorInfo = new GeneratorDetailVO.GeneratorInfo();
        generatorInfo.setHasImport("1");
        generatorInfo.setHasExport("0");
        detailVO.setGeneratorInfo(generatorInfo);

        GeneratorDetailVO.Column idColumn = new GeneratorDetailVO.Column();
        idColumn.setJavaType("Long");
        idColumn.setTsType("number");
        idColumn.setJavaField("id");
        idColumn.setIsPk("1");
        idColumn.setIsImport("0");

        GeneratorDetailVO.Column nameColumn = new GeneratorDetailVO.Column();
        nameColumn.setJavaType("String");
        nameColumn.setJavaField("customerName");
        nameColumn.setColumnComment("客户名称");
        nameColumn.setIsPk("0");
        nameColumn.setIsImport("1");
        nameColumn.setIsRequired("1");

        GeneratorDetailVO.Column orderNoColumn = new GeneratorDetailVO.Column();
        orderNoColumn.setJavaType("String");
        orderNoColumn.setJavaField("orderNo");
        orderNoColumn.setColumnComment("订单编号");
        orderNoColumn.setIsPk("0");
        orderNoColumn.setIsImport("1");
        orderNoColumn.setIsRequired("1");
        orderNoColumn.setIsUniqueValid("1");

        detailVO.setColumns(List.of(idColumn, nameColumn, orderNoColumn));

        Map<String, Object> model = new CodeModelBuilder()
                .builderDynamicsParam(detailVO)
                .getModel();

        assertThat(model.get("importBizKeyColumn")).isSameAs(orderNoColumn);
    }

    @Test
    void builderDynamicsParamShouldExposePrimaryKeyJavaTypeForDetailEndpoint() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.GeneratorInfo generatorInfo = new GeneratorDetailVO.GeneratorInfo();
        generatorInfo.setHasImport("0");
        generatorInfo.setHasExport("0");
        detailVO.setGeneratorInfo(generatorInfo);

        GeneratorDetailVO.Column idColumn = new GeneratorDetailVO.Column();
        idColumn.setJavaType("Long");
        idColumn.setTsType("number");
        idColumn.setJavaField("id");
        idColumn.setIsPk("1");
        detailVO.setColumns(List.of(idColumn));

        Map<String, Object> model = new CodeModelBuilder()
                .builderDynamicsParam(detailVO)
                .getModel();

        assertThat(model.get("idJavaType")).isEqualTo("Long");
        assertThat(model.get("idType")).isEqualTo("number");
    }

    @Test
    void indexVueTemplateShouldUseFieldNameAsFileUploadSlotName() throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/vue/index.vue.ftl")) {
            assertThat(stream).isNotNull();
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(template).contains("<template #${field.javaField}=\"{ row }\">");
            assertThat(template).contains("align=\"${downloadAlign}\"");
            assertThat(template).contains("<#if field.htmlType == \"fileUpload\" || field.htmlType == \"imageUpload\">");
            assertThat(template).contains("type: 'daterange'");
            assertThat(template).contains("valueFormat: 'YYYY-MM-DD'");
            assertThat(template).contains("<#elseif field.searchType == \"time-picker\">");
            assertThat(template).contains("isRange: true");
            assertThat(template).contains("valueFormat: 'HH:mm:ss'");
            assertThat(template).contains("value: 'alias'");
            assertThat(template).doesNotContain("<template #url=\"{ row }\">");
            assertThat(template).doesNotContain(":align=\"'${field.options['file-download-list.align']!''}'\"");
        }
    }

    @Test
    void componentFormTemplateShouldUseMatchingDateAndTimeValueFormats() throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/vue/componentForm.vue.ftl")) {
            assertThat(stream).isNotNull();
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(template).contains("type=\"date\"");
            assertThat(template).contains("value-format=\"YYYY-MM-DD\"");
            assertThat(template).contains("value-format=\"HH:mm:ss\"");
            assertThat(template).contains("<#elseif field.htmlType == \"fileUpload\" || field.htmlType == \"imageUpload\">");
            assertThat(template).contains("<#elseif field.htmlType == \"radio\" || field.htmlType == \"radio-group\">");
            assertThat(template).contains("<el-radio-group v-model=\"paramsProps.row.${field.javaField}\">");
            assertThat(template).contains("<#elseif field.htmlType == \"checkbox\">");
            assertThat(template).contains("<el-checkbox-group");
            assertThat(template).contains("v-model=\"${field.javaField}CheckedValues\"");
            assertThat(template).contains("@change=\"syncCheckboxValue('${field.javaField}', $event)\"");
            assertThat(template).contains("normalizeCheckboxValue(params.row.${field.javaField}");
            assertThat(template).contains("syncCheckboxValue('${field.javaField}', ${field.javaField}CheckedValues.value, false);");
            assertThat(template).contains("paramsProps.value.row.${field.javaField} = formatCheckboxValue(${field.javaField}CheckedValues.value);");
            assertThat(template).contains("const syncCheckboxValue = (fieldName: string, value: unknown, validate = true) => {");
            assertThat(template).contains("const validate${field.upCamelField}Checked = (_rule: unknown, _value: unknown, callback: (error?: Error) => void) => {");
            assertThat(template).contains("normalizeCheckboxValue(paramsProps.value.row.${field.javaField}");
            assertThat(template).contains("validator: validate${field.upCamelField}Checked, trigger: 'change'");
            assertThat(template).contains("@change=\"syncUploadValue('${field.javaField}', $event)\"");
            assertThat(template).contains("@update:modelValue=\"syncUploadValue('${field.javaField}', $event)\"");
            assertThat(template).contains("const normalizeUploadValue = (value: unknown): IResourceUploadResult[] | string[] => {");
            assertThat(template).contains("const hasUploadValue = (value: unknown) => normalizeUploadValue(value).length > 0;");
            assertThat(template).contains("const syncUploadValue = (fieldName: string, value: unknown, validate = true) => {");
            assertThat(template).contains("if (!hasUploadValue(paramsProps.value.row.${field.javaField})) {");
            assertThat(template).contains(":height=\"'${field.options['height']!'400px'}'\"");
            assertThat(template).doesNotContain("<#elseif field.htmlType == \"select\" || field.htmlType == \"radio\">");
        }
    }

    @Test
    void builderDynamicsParamShouldImportOptionsStoreForRadioGroupAndCheckbox() {
        GeneratorDetailVO detailVO = new GeneratorDetailVO();
        GeneratorDetailVO.GeneratorInfo generatorInfo = new GeneratorDetailVO.GeneratorInfo();
        generatorInfo.setHasImport("0");
        generatorInfo.setHasExport("0");
        detailVO.setGeneratorInfo(generatorInfo);

        GeneratorDetailVO.Column radioGroupColumn = new GeneratorDetailVO.Column();
        radioGroupColumn.setHtmlType("radio-group");
        radioGroupColumn.setDictType("yes_no");
        radioGroupColumn.setIsPk("0");
        radioGroupColumn.setIsImport("0");
        radioGroupColumn.setIsExport("0");

        GeneratorDetailVO.Column checkboxColumn = new GeneratorDetailVO.Column();
        checkboxColumn.setHtmlType("checkbox");
        checkboxColumn.setDictType("yes_no");
        checkboxColumn.setIsPk("0");
        checkboxColumn.setIsImport("0");
        checkboxColumn.setIsExport("0");

        detailVO.setColumns(List.of(radioGroupColumn, checkboxColumn));

        Map<String, Object> model = new CodeModelBuilder()
                .builderDynamicsParam(detailVO)
                .getModel();

        assertThat(model.get("hasSelect")).isEqualTo(true);
    }

    @Test
    void apiExcelTemplatesShouldIgnoreUploadResourceListFields() throws IOException {
        try (InputStream voStream = getClass().getClassLoader().getResourceAsStream("templates/api/vo.java.ftl");
                InputStream importStream = getClass().getClassLoader().getResourceAsStream("templates/api/dtoImport.java.ftl")) {
            assertThat(voStream).isNotNull();
            assertThat(importStream).isNotNull();
            String voTemplate = new String(voStream.readAllBytes(), StandardCharsets.UTF_8);
            String importTemplate = new String(importStream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(voTemplate).contains("<#if field.javaType?starts_with(\"List\") && hasExcel == true>");
            assertThat(importTemplate).contains("<#if field.javaType?starts_with(\"List\")>");
            assertThat(importTemplate).contains("import cn.idev.excel.annotation.ExcelIgnore;");
        }
    }

    @Test
    void excelImporterTemplateShouldDependOnExcelImportSpiInsteadOfAdminModule() throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/api/excelImporter.java.ftl")) {
            assertThat(stream).isNotNull();
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(template).contains("import com.sz.excel.imports.spi.ImportBatchTracker;");
            assertThat(template).contains("import com.sz.excel.imports.spi.ImportFailRecordWriter;");
            assertThat(template).contains("ImportBatchTracker importBatchTracker");
            assertThat(template).contains("ImportFailRecordWriter importFailRecordWriter");
            assertThat(template).contains("super(importBatchTracker, importFailRecordWriter);");
            assertThat(template).doesNotContain("com.sz.admin.system.service");
            assertThat(template).doesNotContain("SysImportBatchService");
            assertThat(template).doesNotContain("SysImportFailRecordService");
        }
    }
}
