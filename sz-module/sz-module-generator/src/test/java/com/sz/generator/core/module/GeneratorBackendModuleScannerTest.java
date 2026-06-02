package com.sz.generator.core.module;

import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.pojo.vo.GeneratorBackendModuleOptionVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorBackendModuleScannerTest {

    @TempDir
    Path projectRoot;

    @Test
    void scanShouldReturnGeneratableModulesAndHideFrameworkModules() throws IOException {
        write("pom.xml", """
                <project>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>com.sz</groupId>
                        <artifactId>sz-module-audit</artifactId>
                        <version>${project.version}</version>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                </project>
                """);
        write("sz-module/pom.xml", """
                <project>
                  <modules>
                    <module>sz-module-audit</module>
                  </modules>
                </project>
                """);
        write("sz-service/sz-service-admin/pom.xml", """
                <project>
                  <dependencies>
                    <dependency>
                      <artifactId>sz-module-audit</artifactId>
                    </dependency>
                  </dependencies>
                </project>
                """);
        write("sz-service/sz-service-admin/src/main/resources/db/changelog/changelog-master.xml", """
                <databaseChangeLog>
                  <include file="db/changelog/module-audit-changelog.xml" relativeToChangelogFile="false"/>
                </databaseChangeLog>
                """);
        write("sz-service/sz-service-admin/src/main/resources/application.yml", """
                sz:
                  api-prefix:
                    modules:
                      audit:
                        prefix: /platform-audit/
                """);
        write("sz-module/sz-module-audit/pom.xml", "<project/>");
        write("sz-module/sz-module-audit/src/main/resources/db/changelog/module-audit-changelog.xml", "<databaseChangeLog/>");
        write("sz-module/sz-module-audit/src/main/java/com/sz/audit/config/AuditConfiguration.java", """
                package com.sz.audit.config;

                import com.sz.core.common.web.ApiPrefixRegister;
                import org.mybatis.spring.annotation.MapperScan;

                @MapperScan(basePackages = new String[] { "com.sz.audit.controller" })
                class AuditConfiguration {
                    ApiPrefixRegister auditApiPrefixRegister() {
                        return new ApiPrefixRegister() {
                            public String module() {
                                return "audit";
                            }

                            public String prefix() {
                                return "/audit";
                            }
                        };
                    }
                }
                """);
        write("sz-module/sz-module-demo/pom.xml", "<project/>");
        write("sz-module/sz-module-common/pom.xml", "<project/>");
        write("sz-module/sz-module-generator/pom.xml", "<project/>");

        List<GeneratorBackendModuleOptionVO> options = scanner().scan(projectRoot);

        assertThat(options).extracting(GeneratorBackendModuleOptionVO::getModuleName)
                .containsExactly("sz-module-audit", "sz-module-demo")
                .doesNotContain("sz-module-common", "sz-module-generator");

        GeneratorBackendModuleOptionVO audit = find(options, "sz-module-audit");
        assertThat(audit.getStatus()).isEqualTo(GeneratorBackendModuleScanner.STATUS_READY);
        assertThat(audit.getMissingItems()).isEmpty();
        assertThat(audit.getPackageName()).isEqualTo("com.sz.audit");
        assertThat(audit.getApiPrefixModule()).isEqualTo("audit");
        assertThat(audit.getApiPrefix()).isEqualTo("/platform-audit");
        assertThat(audit.getRecommended()).isTrue();

        GeneratorBackendModuleOptionVO demo = find(options, "sz-module-demo");
        assertThat(demo.getStatus()).isEqualTo(GeneratorBackendModuleScanner.STATUS_PENDING);
        assertThat(demo.getMissingItems()).contains("parent-module", "dependency-management", "service-dependency", "api-prefix", "mapper-scan",
                "liquibase-include");
    }

    private GeneratorBackendModuleScanner scanner() {
        GeneratorProperties properties = new GeneratorProperties();
        properties.setModuleName("sz-module");
        return new GeneratorBackendModuleScanner(properties);
    }

    private GeneratorBackendModuleOptionVO find(List<GeneratorBackendModuleOptionVO> options, String moduleName) {
        return options.stream()
                .filter(option -> moduleName.equals(option.getModuleName()))
                .findFirst()
                .orElseThrow();
    }

    private void write(String relativePath, String content) throws IOException {
        Path file = projectRoot.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }
}
