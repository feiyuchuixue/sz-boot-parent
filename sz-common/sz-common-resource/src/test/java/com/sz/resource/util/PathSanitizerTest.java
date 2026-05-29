package com.sz.resource.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathSanitizerTest {

    @Test
    void objectKeyRejectsTraversalAndUnsafeSegments() {
        assertThat(PathSanitizer.validate("avatars/20260526/logo.png", PathSanitizer.Mode.OBJECT_KEY)).isTrue();

        assertThatThrownBy(() -> PathSanitizer.validate("../secret.txt", PathSanitizer.Mode.OBJECT_KEY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("父目录引用");
        assertThatThrownBy(() -> PathSanitizer.validate("avatars\\.env", PathSanitizer.Mode.OBJECT_KEY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("反斜杠");
        assertThatThrownBy(() -> PathSanitizer.validate("avatars/.env", PathSanitizer.Mode.OBJECT_KEY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("隐藏文件");
    }

    @Test
    void httpPathReturnsFalseInsteadOfThrowing() {
        assertThat(PathSanitizer.validate("avatars/logo.png", PathSanitizer.Mode.HTTP_PATH)).isTrue();
        assertThat(PathSanitizer.validate("%2e%2e/secret.txt", PathSanitizer.Mode.HTTP_PATH)).isFalse();
        assertThat(PathSanitizer.validate("%252e%252e/secret.txt", PathSanitizer.Mode.HTTP_PATH)).isFalse();
        assertThat(PathSanitizer.validate("%", PathSanitizer.Mode.HTTP_PATH)).isFalse();
        assertThat(PathSanitizer.validate(null, PathSanitizer.Mode.HTTP_PATH)).isFalse();
    }

    @Test
    void configAllowsBlankButRejectsUnsafeValues() {
        assertThat(PathSanitizer.validate(null, PathSanitizer.Mode.CONFIG)).isTrue();
        assertThat(PathSanitizer.validate("avatars", PathSanitizer.Mode.CONFIG)).isTrue();

        assertThatThrownBy(() -> PathSanitizer.validate("/etc/passwd", PathSanitizer.Mode.CONFIG))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("绝对路径");
    }
}
