package com.sz.generator.core;

import com.sz.generator.pojo.vo.CodeGenTempResult;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
 * @ClassName CodeGenerationTemplate
 * @Author sz
 * @Date 2024/1/15 15:51
 * @Version 1.0
 */
public abstract class AbstractCodeGenerationTemplate {

    private FreeMarkerConfigurer configurer;

    private String rootPath;

    private GeneratorDetailVO detailVO;

    private Map<String, Object> model;

    public AbstractCodeGenerationTemplate(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        this.configurer = configurer;
        this.rootPath = rootPath;
        this.detailVO = detailVO;
        this.model = model;
    }

    /**
     * 模板名
     *
     * @return
     */
    protected abstract String getTemplateFileName();

    /**
     * 输出文件名
     *
     * @param model
     * @return
     */
    protected abstract String getOutputFileName(Map<String, Object> model);

    /**
     * 开发环境对应的前缀路径 如 java 的 src/main/java
     *
     * @return
     */
    protected abstract String getProjectPrefix();

    /**
     * 文件扩展名
     *
     * @return
     */
    protected abstract String getExtension();

    /**
     * 别名
     *
     * @return
     */
    protected abstract String alias();

    protected abstract String language();

    /**
     * zip要存储的目录
     *
     * @return
     */
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
            if (getZipParentPackage() != null && !("").equals(getZipParentPackage().trim())) {
                zipPath = Paths.get(getZipParentPackage(), zipPath).toString();
            }
        }
        return new CodeGenTempResult(template, zipPath, getExtension(), alias(), language(), outputMsg);
    }

    /**
     * 构建绝对路径 （完整路径）
     *
     * @param rootPath
     * @param projectPrefix
     * @param zipPath
     * @return
     */
    private static String buildAbsoluteOutputFilePath(String rootPath, String projectPrefix, String zipPath) {
        return Paths.get(rootPath, projectPrefix, zipPath.toString()).toString();
    }

    /**
     * 构建相对路径（zip存储路径）
     *
     * @param outputPackage
     * @param outputClassName
     * @param extension
     * @return
     */
    private static String buildRelativeOutputFilePath(String outputPackage, String outputClassName, String extension) {
        return Paths.get(outputPackage.replace(".", File.separator), outputClassName + extension).toString();
    }

    /**
     * 生成文件
     *
     * @param outputPath
     * @param template
     * @param model
     * @throws IOException
     */
    private static String saveToFile(String outputPath, Template template, Map<String, Object> model) throws IOException {
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
                System.out.println(info);
                return info; // Do nothing or throw an exception based on your requirement
            }

            // Write the template content to the file
            try (Writer writer = new FileWriter(outputPath.toString())) {
                template.process(model, writer);
                System.out.println("Output Path: " + outputPath);
                return "Output Path: " + outputPath;
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "Output Err: " + outputPath;
        }
    }

}
