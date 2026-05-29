package com.sz.generator.core;

import com.sz.generator.pojo.vo.CodeGenTempResult;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author sz
 * @since 2024/1/15 15:51
 */
@Slf4j
public abstract class AbstractCodeGenerationTemplate {

    private final FreeMarkerConfigurer configurer;

    private final String rootPath;

    @Getter
    private final GeneratorDetailVO detailVO;

    private final Map<String, Object> model;

    protected AbstractCodeGenerationTemplate(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        this.configurer = configurer;
        this.rootPath = rootPath;
        this.detailVO = detailVO;
        this.model = model;
    }

    // 模板名
    protected abstract String getTemplateFileName();

    // 输出文件名
    protected abstract String getOutputFileName(Map<String, Object> model);

    // 开发环境对应的前缀路径 如 java 的 src/main/java
    protected abstract String getProjectPrefix();

    // 文件扩展名
    protected abstract String getExtension();

    // 别名
    protected abstract String alias();

    protected abstract String language();

    // zip要存储的目录
    protected abstract String getZipParentPackage();

    protected abstract String getOutputPackage(Map<String, Object> model);

    public CodeGenTempResult buildTemplate(boolean isSaveToLocal) throws IOException {
        Template template = configurer.getConfiguration().getTemplate(getTemplateFileName());
        String outputPackage = getOutputPackage(model);
        String outputClassName = getOutputFileName(model);
        String zipPath = buildRelativeOutputFilePath(outputPackage, outputClassName, getExtension());

        String outputMsg = "";
        // 本地环境，保存代码到指定目录下
        if (isSaveToLocal) {
            String outputPath = buildAbsoluteOutputFilePath(rootPath, getProjectPrefix(), zipPath);
            outputMsg = saveToFile(outputPath, template, model);
        } else {
            if (getZipParentPackage() != null && !getZipParentPackage().trim().isEmpty()) {
                zipPath = Paths.get(getZipParentPackage(), zipPath).toString();
            }
        }
        return new CodeGenTempResult(template, zipPath, getExtension(), alias(), language(), outputMsg);
    }

    /**
     * 构建绝对路径（完整路径）。
     *
     * 该方法根据提供的根路径、项目前缀和压缩文件路径，生成完整的绝对路径字符串。
     *
     * @param rootPath
     *            根路径，通常为文件系统的基础路径
     * @param projectPrefix
     *            项目前缀，用于区分不同项目的路径
     * @param zipPath
     *            压缩文件的相对路径
     * @return 生成的完整绝对路径字符串
     */
    private static String buildAbsoluteOutputFilePath(String rootPath, String projectPrefix, String zipPath) {
        return Paths.get(rootPath, projectPrefix, zipPath).toString();
    }

    /**
     * 构建相对路径（用于存储 ZIP 文件的路径）。
     *
     * 该方法根据输出包路径、输出类名和文件扩展名，生成用于存储 ZIP 文件的相对路径。
     *
     * @param outputPackage
     *            输出包路径，表示文件所在的包结构
     * @param outputClassName
     *            输出类名，用于构成文件名的一部分
     * @param extension
     *            文件扩展名，例如 ".zip" 或其他文件类型
     * @return 生成的 ZIP 文件存储的相对路径字符串
     */
    private static String buildRelativeOutputFilePath(String outputPackage, String outputClassName, String extension) {
        return Paths.get(outputPackage.replace(".", File.separator), outputClassName + extension).toString();
    }

    /**
     * 生成文件。
     *
     * 该方法根据提供的模板和数据模型，生成文件并保存到指定的输出路径。
     *
     * @param outputPath
     *            文件的输出路径，指定生成的文件保存的位置
     * @param template
     *            使用的模板，用于生成文件的内容
     * @param model
     *            数据模型，填充模板中的占位符
     */
    private static String saveToFile(String outputPath, Template template, Map<String, Object> model) {
        try {
            Path path = Paths.get(outputPath);
            // Get the parent directory of the output file
            Path parentDirectory = path.getParent();
            // Check if the parent directory exists, create it if not
            if (!Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            // Check if the file already exists
            if (Files.exists(path)) {
                // File already exists, handle accordingly (e.g., change filename, throw
                // exception)
                String info = "File already exists: " + outputPath;
                log.error(info);
                return info; // Do nothing or throw an exception based on your requirement
            }

            // Write the template content to the file
            try (Writer writer = new FileWriter(outputPath)) {
                template.process(model, writer);
                log.info("Output Path: {}", outputPath);
                return "Output Path: " + outputPath;
            }

        } catch (IOException | TemplateException e) {
            log.error("saveToFile err", e);
            return "Output Err: " + outputPath;
        }
    }

}
