package com.sz.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AppVersionUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void resolveKeepsExplicitVersion() {
        assertThat(AppVersionUtils.resolve(" 2.0.0 ", AppVersionUtilsTest.class)).isEqualTo("2.0.0");
    }

    @Test
    void resolveReadsRevisionWhenConfiguredVersionStillContainsBuildToken() throws Exception {
        String originalUserDir = System.getProperty("user.dir");
        try {
            Files.writeString(tempDir.resolve("pom.xml"), """
                    <project>
                        <properties>
                            <revision>2.0.0</revision>
                        </properties>
                    </project>
                    """);
            System.setProperty("user.dir", tempDir.toString());

            assertThat(AppVersionUtils.resolve("@project.version@", null)).isEqualTo("2.0.0");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
