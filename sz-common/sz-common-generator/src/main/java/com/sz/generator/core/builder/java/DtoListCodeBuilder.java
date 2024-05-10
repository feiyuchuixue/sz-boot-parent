package com.sz.generator.core.builder.java;

import com.sz.generator.core.AbstractCodeGenerationTemplate;
import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.util.Map;

/**
 * @ClassName PoCodeBuilder
 * @Author sz
 * @Date 2024/1/16 8:02
 * @Version 1.0
 */
public class DtoListCodeBuilder extends AbstractCodeGenerationTemplate {

    public DtoListCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "api" + File.separator + "dtoList.java.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return model.get("dtoListClassName").toString();
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return model.get("dtoPkg").toString();
    }

    @Override
    protected String getProjectPrefix() {
        return GeneratorConstants.PROJECT_JAVA_PREFIX;
    }

    @Override
    protected String getExtension() {
        return ".java";
    }

    @Override
    protected String getZipParentPackage() {
        return "java";
    }

    @Override
    protected String alias() {
        return "dtoList";
    }

    @Override
    protected String language() {
        return "java";
    }

}
