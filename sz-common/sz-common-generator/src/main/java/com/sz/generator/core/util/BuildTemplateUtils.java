package com.sz.generator.core.util;

import com.sz.generator.core.AbstractCodeGenerationTemplate;
import com.sz.generator.core.builder.java.*;
import com.sz.generator.core.builder.vue.FormCodeBuilder;
import com.sz.generator.core.builder.vue.IndexCodeBuilder;
import com.sz.generator.core.builder.vue.InterfaceCodeBuilder;
import com.sz.generator.core.builder.vue.ModulesCodeBuilder;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.*;

/**
 * @ClassName BuildApiTemplateUtils
 * @Author sz
 * @Date 2023/12/18 10:21
 * @Version 1.0
 */
public class BuildTemplateUtils {

    /**
     * java模版
     *
     * @param configurer
     * @param rootPath
     * @param detailVO
     * @param model
     * @return
     */
    public static List<AbstractCodeGenerationTemplate> getApiTemplates(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO,
            Map<String, Object> model) {
        boolean hasImport = ("1").equals(detailVO.getGeneratorInfo().getHasImport());
        List<AbstractCodeGenerationTemplate> list = new ArrayList<>(Arrays.asList(new ControllerCodeBuilder(configurer, rootPath, detailVO, model),
                new ServiceImplCodeBuilder(configurer, rootPath, detailVO, model), new ServiceCodeBuilder(configurer, rootPath, detailVO, model),
                new MapperCodeBuilder(configurer, rootPath, detailVO, model), new MapperXmlCodeBuilder(configurer, rootPath, detailVO, model),
                new DtoCreateCodeBuilder(configurer, rootPath, detailVO, model), new DtoListCodeBuilder(configurer, rootPath, detailVO, model),
                new DtoUpdateCodeBuilder(configurer, rootPath, detailVO, model)));
        if (hasImport)
            list.add(new DtoImportCodeBuilder(configurer, rootPath, detailVO, model));
        list.add(new PoCodeBuilder(configurer, rootPath, detailVO, model));
        list.add(new VoCodeBuilder(configurer, rootPath, detailVO, model));

        return switch (detailVO.getGeneratorInfo().getGenerateType()) {
            case "all", "server" -> list;
            case "service" -> Arrays.asList(new ServiceCodeBuilder(configurer, rootPath, detailVO, model),
                    new ServiceImplCodeBuilder(configurer, rootPath, detailVO, model), new MapperCodeBuilder(configurer, rootPath, detailVO, model),
                    new MapperXmlCodeBuilder(configurer, rootPath, detailVO, model), new PoCodeBuilder(configurer, rootPath, detailVO, model));
            case "db" -> Arrays.asList(new PoCodeBuilder(configurer, rootPath, detailVO, model), new MapperCodeBuilder(configurer, rootPath, detailVO, model),
                    new MapperXmlCodeBuilder(configurer, rootPath, detailVO, model));
            default -> Collections.emptyList();
        };
    }

    /**
     * web模版
     *
     * @param configurer
     * @param rootPath
     * @param detailVO
     * @param model
     * @return
     */
    public static List<AbstractCodeGenerationTemplate> getWebTemplates(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO,
            Map<String, Object> model) {
        List<AbstractCodeGenerationTemplate> list = new ArrayList<>(
                Arrays.asList(new IndexCodeBuilder(configurer, rootPath, detailVO, model), new FormCodeBuilder(configurer, rootPath, detailVO, model),
                        new InterfaceCodeBuilder(configurer, rootPath, detailVO, model), new ModulesCodeBuilder(configurer, rootPath, detailVO, model)));
        return switch (detailVO.getGeneratorInfo().getGenerateType()) {
            case "all" -> list;
            default -> Collections.emptyList();
        };
    }

}
