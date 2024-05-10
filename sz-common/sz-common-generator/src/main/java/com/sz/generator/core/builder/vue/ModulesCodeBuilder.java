package com.sz.generator.core.builder.vue;

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
public class ModulesCodeBuilder extends AbstractCodeGenerationTemplate {

    public ModulesCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "vue" + File.separator + "modules.ts.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return model.get("modulesClassName").toString();
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return model.get("modulesPkg").toString();
    }

    @Override
    protected String getProjectPrefix() {
        return GeneratorConstants.PROJECT_WEB_PREFIX;
    }

    @Override
    protected String getExtension() {
        return ".ts";
    }

    @Override
    protected String getZipParentPackage() {
        return "vue";
    }

    @Override
    protected String alias() {
        return "module.ts";
    }

    @Override
    protected String language() {
        return "ts";
    }
}
