package com.sz.generator.core.script;

import com.sz.generator.config.ConditionalOnGeneratorEnabled;
import com.sz.generator.pojo.vo.ScriptExportItemVO;
import com.sz.generator.pojo.vo.ScriptExportVO;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Renders XML and SQL scripts from FreeMarker templates.
 */
@ConditionalOnGeneratorEnabled
@Service
@RequiredArgsConstructor
public class ScriptExportService {

    private static final String MENU_XML_TEMPLATE = "liquibase/menuImport.xml.ftl";

    private static final String MENU_INIT_XML_TEMPLATE = "liquibase/menuInit.xml.ftl";

    private static final String MENU_SQL_TEMPLATE = "sql/menuImport.sql.ftl";

    private static final String MENU_INIT_SQL_TEMPLATE = "sql/menuInit.sql.ftl";

    private static final String DICT_XML_TEMPLATE = "liquibase/dictImport.xml.ftl";

    private static final String DICT_SQL_TEMPLATE = "sql/dictImport.sql.ftl";

    private final FreeMarkerConfigurer configurer;

    private final ScriptDialectResolver dialectResolver;

    public ScriptExportVO renderMenuExport(Map<String, Object> model, String requestedDialect) throws IOException {
        return render(model, requestedDialect, MENU_XML_TEMPLATE, MENU_SQL_TEMPLATE, "menu", "菜单脚本");
    }

    public ScriptExportVO renderMenuInit(Map<String, Object> model, String requestedDialect) throws IOException {
        return render(model, requestedDialect, MENU_INIT_XML_TEMPLATE, MENU_INIT_SQL_TEMPLATE, "menuInit", "菜单初始化脚本");
    }

    public ScriptExportVO renderDictExport(Map<String, Object> model, String requestedDialect) throws IOException {
        return render(model, requestedDialect, DICT_XML_TEMPLATE, DICT_SQL_TEMPLATE, "dictImport", "字典脚本");
    }

    public String renderMenuSql(Map<String, Object> model, String requestedDialect) throws IOException {
        return renderSql(model, requestedDialect, MENU_SQL_TEMPLATE);
    }

    public String renderDictSql(Map<String, Object> model, String requestedDialect) throws IOException {
        return renderSql(model, requestedDialect, DICT_SQL_TEMPLATE);
    }

    private ScriptExportVO render(Map<String, Object> model, String requestedDialect, String xmlTemplate, String sqlTemplate, String baseName, String title)
            throws IOException {
        ScriptDialect currentDialect = dialectResolver.resolveCurrent();
        ScriptDialect selectedDialect = dialectResolver.resolveSelected(requestedDialect);
        Map<String, Object> enrichedModel = enrichModel(model, selectedDialect);

        ScriptExportVO vo = new ScriptExportVO();
        vo.setCurrentDialect(currentDialect.getCode());
        vo.setSelectedDialect(selectedDialect.getCode());
        vo.getItems()
                .add(item("xml", null, "xml", "Liquibase XML", "liquibase" + File.separator + baseName + ".xml", renderTemplate(xmlTemplate, enrichedModel)));
        vo.getItems().add(item("sql", selectedDialect.getCode(), "sql", title + " SQL (" + selectedDialect.getCode() + ")",
                "sql" + File.separator + baseName + "." + selectedDialect.getCode() + ".sql", renderTemplate(sqlTemplate, enrichedModel)));
        return vo;
    }

    private String renderSql(Map<String, Object> model, String requestedDialect, String sqlTemplate) throws IOException {
        ScriptDialect selectedDialect = dialectResolver.resolveSelected(requestedDialect);
        return renderTemplate(sqlTemplate, enrichModel(model, selectedDialect));
    }

    private Map<String, Object> enrichModel(Map<String, Object> model, ScriptDialect selectedDialect) {
        Map<String, Object> enriched = new HashMap<>(model);
        enriched.put("scriptDialect", selectedDialect.getCode());
        enriched.put("sql", new SqlValueFormatter());
        return enriched;
    }

    private String renderTemplate(String templateName, Map<String, Object> model) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            Template template = configurer.getConfiguration().getTemplate(templateName);
            template.process(model, writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException("Error rendering script template: " + templateName, e);
        }
    }

    private static ScriptExportItemVO item(String format, String dialect, String language, String title, String fileName, String content) {
        ScriptExportItemVO item = new ScriptExportItemVO();
        item.setFormat(format);
        item.setDialect(dialect);
        item.setLanguage(language);
        item.setTitle(title);
        item.setFileName(fileName);
        item.setContent(content);
        return item;
    }
}
