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
 * @author sz
 * @since 2023/12/18 10:21
 */
public class BuildTemplateUtils {

    private BuildTemplateUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 使用 Java 模板生成内容。
     *
     * 该方法根据提供的模板配置、根路径和详细信息对象，生成指定的模型内容。
     *
     * @param configurer
     *            模板配置对象，用于设置模板生成的参数
     * @param rootPath
     *            模板的根路径，指定模板文件的存放位置
     * @param detailVO
     *            包含详细信息的数据对象，用于填充模板
     * @param model
     *            模板中使用的数据模型
     * @return 生成的模板内容，通常为字符串或其他格式
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
     * 使用 Web 模板生成内容。
     *
     * 该方法根据提供的模板配置、根路径和详细信息对象，生成指定的 Web 模板内容。
     *
     * @param configurer
     *            模板配置对象，用于设置模板生成的参数
     * @param rootPath
     *            模板的根路径，指定模板文件的存放位置
     * @param detailVO
     *            包含详细信息的数据对象，用于填充模板
     * @param model
     *            模板中使用的数据模型
     * @return 生成的 Web 模板内容，通常为 HTML 字符串或其他格式
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
