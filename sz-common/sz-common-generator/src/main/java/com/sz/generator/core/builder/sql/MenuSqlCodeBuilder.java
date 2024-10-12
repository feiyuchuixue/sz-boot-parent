package com.sz.generator.core.builder.sql;

import com.sz.generator.core.AbstractCodeGenerationTemplate;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.util.Map;

public class MenuSqlCodeBuilder extends AbstractCodeGenerationTemplate {

    public MenuSqlCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "sql" + File.separator + "menuInit.sql.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return "menuInit";
    }

    @Override
    protected String getProjectPrefix() {
        return null;
    }

    @Override
    protected String getExtension() {
        return ".sql";
    }

    @Override
    protected String alias() {
        return "sql";
    }

    @Override
    protected String language() {
        return "sql";
    }

    @Override
    protected String getZipParentPackage() {
        return "sql";
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return "";
    }

}
