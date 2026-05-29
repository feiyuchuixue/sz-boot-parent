package com.sz.admin.system.business;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private static String read(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}
