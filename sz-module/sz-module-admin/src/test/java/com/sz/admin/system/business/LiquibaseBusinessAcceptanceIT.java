package com.sz.admin.system.business;

import com.sz.resource.model.ResourceRef;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import tools.jackson.databind.json.JsonMapper;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseBusinessAcceptanceIT {

    @Test
    void frameworkInitIncludesCoreBusinessTablesDescribedByDocs() throws IOException {
        String frameworkInit = read("src/main/resources/db/changelog/framework/2.0.0/001_framework_init.xml");

        assertThat(frameworkInit).contains("003_sys_data_role_relation.xml", "010_sys_dict.xml", "011_sys_dict_type.xml", "013_sys_import_batch.xml",
                "014_sys_import_fail_record.xml", "016_sys_menu.xml", "019_sys_resource.xml", "021_sys_role_menu.xml", "025_sys_dict_source.xml");
    }

    @Test
    void coreBusinessTablesKeepRequiredColumnsForDocsContracts() throws IOException {
        assertThat(read("src/main/resources/db/changelog/framework/2.0.0/tables/016_sys_menu.xml")).contains("use_data_scope");
        assertThat(read("src/main/resources/db/changelog/framework/2.0.0/tables/021_sys_role_menu.xml")).contains("permission_type", "data_scope_cd");
        assertThat(read("src/main/resources/db/changelog/framework/2.0.0/tables/003_sys_data_role_relation.xml")).contains("role_id", "menu_id",
                "relation_type_cd", "relation_id");
        assertThat(read("src/main/resources/db/changelog/framework/2.0.0/tables/025_sys_dict_source.xml")).contains("sys_dict_source", "source_code");
        assertThat(read("src/main/resources/db/changelog/framework/2.0.0/tables/019_sys_resource.xml")).contains("sys_resource", "scene_code", "object_key");
    }

    @Test
    void teacherDemoUpgradeReferencesRegisteredResourcesWithoutChangingAttachments() throws Exception {
        Path upgrade = Path.of("src/main/resources/db/changelog/demo/2.1.0/001_demo_resource_references.xml");
        assertThat(upgrade).exists();
        assertThat(read("src/main/resources/db/changelog/demo/changelog-master.xml")).contains("2.1.0/001_demo_resource_references.xml");
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(upgrade.toFile());
        Map<Long, Map<String, String>> resources = new LinkedHashMap<>();
        var inserts = document.getElementsByTagName("insert");
        for (int i = 0; i < inserts.getLength(); i++) {
            Map<String, String> row = columns((Element) inserts.item(i));
            resources.put(Long.valueOf(row.get("id")), row);
        }
        assertThat(resources).hasSize(8);
        var updates = document.getElementsByTagName("update");
        int teacherUpdates = 0;
        for (int i = 0; i < updates.getLength(); i++) {
            Element update = (Element) updates.item(i);
            if (!"teacher_statistics".equals(update.getAttribute("tableName"))) {
                continue;
            }
            long teacherId = teacherUpdates++ == 0 ? 25L : 26L;
            List<ResourceRef> refs = references(columns(update).get("url"));
            assertThat(refs).hasSize(teacherId == 25 ? 3 : 5);
            for (ResourceRef ref : refs) {
                Map<String, String> resource = resources.get(ref.getResourceId());
                assertThat(resource).as("resource for %s", ref.getOriginName()).isNotNull();
                assertThat(resource).containsEntry("scene_code", "teacher.attachment").containsEntry("object_key", ref.getObjectKey())
                        .containsEntry("origin_name", ref.getOriginName()).containsEntry("content_type", ref.getContentType())
                        .containsEntry("storage_type", "LOCAL").containsEntry("del_flag", "F")
                        .containsEntry("create_id", teacherId == 25 ? "4" : "5");
                assertThat(ref.getAccessUrl()).isNull();
            }
            // 两个数据库的原始附件必须保留顺序、路径和展示信息，仅补资源引用。
            var seed = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(Path.of("src/main/resources/db/changelog/demo/2.0.0/tables/015_teacher_statistics.xml").toFile());
            var rows = seed.getElementsByTagName("insert");
            int matchedSeeds = 0;
            for (int j = 0; j < rows.getLength(); j++) {
                Map<String, String> row = columns((Element) rows.item(j));
                if (Long.toString(teacherId).equals(row.get("id"))) {
                    List<ResourceRef> original = references(row.get("url"));
                    assertThat(refs).usingRecursiveComparison().ignoringFields("resourceId").isEqualTo(original);
                    assertOriginalUrlGuard(update, row.get("url"));
                    assertThat(update.getElementsByTagName("where").item(0).getTextContent())
                            .startsWith("id = " + teacherId + " AND create_id = " + row.get("create_id") + " AND ");
                    matchedSeeds++;
                }
            }
            assertThat(matchedSeeds).isEqualTo(2);
        }
        assertThat(teacherUpdates).isEqualTo(2);
    }

    @Test
    void templateDemoUpgradeUsesExistingResourceForCurrentAndHistoryIndependently() throws Exception {
        Path upgrade = Path.of("src/main/resources/db/changelog/demo/2.1.0/001_demo_resource_references.xml");
        assertThat(upgrade).exists();
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(upgrade.toFile());
        var updates = document.getElementsByTagName("update");
        Map<String, Element> templateUpdates = new LinkedHashMap<>();
        for (int i = 0; i < updates.getLength(); i++) {
            Element update = (Element) updates.item(i);
            if (update.getAttribute("tableName").startsWith("sys_temp_file")) {
                templateUpdates.put(update.getAttribute("tableName"), update);
                List<ResourceRef> refs = references(columns(update).get("url"));
                assertThat(refs).singleElement().satisfies(ref -> {
                    assertThat(ref.getResourceId()).isEqualTo(407693840624005120L);
                    assertThat(ref.getSceneCode()).isEqualTo("template.excel");
                    assertThat(ref.getObjectKey()).isEqualTo("template/20260501/教师统计导入模板新.xlsx");
                });
                String seedFile = "sys_temp_file".equals(update.getAttribute("tableName")) ? "012_sys_temp_file.xml" : "013_sys_temp_file_history.xml";
                var seed = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(Path.of("src/main/resources/db/changelog/demo/2.0.0/tables", seedFile).toFile());
                assertOriginalUrlGuard(update, columns((Element) seed.getElementsByTagName("insert").item(0)).get("url"));
                assertThat(update.getElementsByTagName("where").item(0).getTextContent()).contains("AND create_id = 1 AND ");
            }
        }
        assertThat(templateUpdates).containsOnlyKeys("sys_temp_file", "sys_temp_file_history");
        assertThat(templateUpdates.get("sys_temp_file").getParentNode()).isNotSameAs(templateUpdates.get("sys_temp_file_history").getParentNode());
    }

    private static void assertOriginalUrlGuard(Element update, String originalUrl) throws Exception {
        String digest = HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(originalUrl.getBytes(StandardCharsets.UTF_8)));
        String where = update.getElementsByTagName("where").item(0).getTextContent().trim();
        assertThat(where).endsWith("AND MD5(url) = '" + digest + "'");
        Element changeSet = (Element) update.getParentNode();
        Element preconditions = (Element) changeSet.getElementsByTagName("preConditions").item(0);
        assertThat(preconditions.getAttribute("onFail")).isEqualTo("MARK_RAN");
        assertThat(preconditions.getAttribute("onError")).isEqualTo("HALT");
        assertThat(preconditions.getElementsByTagName("sqlCheck").item(0).getTextContent().trim())
                .isEqualTo("SELECT COUNT(*) FROM " + update.getAttribute("tableName") + " WHERE " + where);
    }

    private static Map<String, String> columns(Element change) {
        Map<String, String> result = new LinkedHashMap<>();
        var columns = change.getElementsByTagName("column");
        for (int i = 0; i < columns.getLength(); i++) {
            Element column = (Element) columns.item(i);
            result.put(column.getAttribute("name"), column.hasAttribute("value") ? column.getAttribute("value") : column.getAttribute("valueNumeric"));
        }
        return result;
    }

    private static List<ResourceRef> references(String json) {
        JsonMapper mapper = JsonMapper.builder().build();
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ResourceRef.class));
    }

    private static String read(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}
