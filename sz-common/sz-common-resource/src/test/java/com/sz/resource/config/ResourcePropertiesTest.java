package com.sz.resource.config;

import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourcePropertiesTest {

    @Test
    void securityDefaultsFallbackWhenNoCustomPolicyConfigured() {
        ResourceProperties.SecurityConfig security = new ResourceProperties.SecurityConfig();

        assertThat(security.getEffectiveAllowedExts()).isSameAs(ResourceSecurityDefaults.DEFAULT_ALLOWED_EXTS);
        assertThat(security.getEffectiveAllowedMimeTypes()).isSameAs(ResourceSecurityDefaults.DEFAULT_ALLOWED_MIMES);
        assertThat(security.getEffectiveMaxSizeBytes()).isEqualTo(ResourceSecurityDefaults.DEFAULT_MAX_SIZE_BYTES);
    }

    @Test
    void securityUsesCustomPolicyWhenConfigured() {
        ResourceProperties.SecurityConfig security = new ResourceProperties.SecurityConfig();
        security.setAllowedExts(Set.of("png"));
        security.setAllowedMimeTypes(Set.of("image/png"));
        security.setMaxSize(DataSize.ofMegabytes(2));

        assertThat(security.getEffectiveAllowedExts()).containsExactly("png");
        assertThat(security.getEffectiveAllowedMimeTypes()).containsExactly("image/png");
        assertThat(security.getEffectiveMaxSizeBytes()).isEqualTo(2L * 1024 * 1024);
    }

    @Test
    void validateBuildsImmutableSceneMapForValidScenes() {
        ResourceProperties properties = new ResourceProperties();
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode("admin.avatar");
        scene.setType(StorageTypeEnum.LOCAL);
        scene.setServeMode(ServeModeEnum.DIRECT);
        scene.setPath("avatars");
        scene.setBaseUrl("http://127.0.0.1/static/avatars");
        properties.setScenes(List.of(scene));

        properties.validate();

        assertThat(properties.getScene("admin.avatar")).isSameAs(scene);
        assertThatThrownBy(() -> properties.getSceneMap().put("x", scene)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void validateRejectsDuplicateSceneCodesAndUnsafePaths() {
        ResourceProperties duplicateProperties = new ResourceProperties();
        duplicateProperties.setScenes(List.of(localScene("admin.avatar", "avatars"), localScene("admin.avatar", "icons")));

        assertThatThrownBy(duplicateProperties::validate).isInstanceOf(IllegalStateException.class).hasMessageContaining("场景编码重复");

        ResourceProperties unsafePathProperties = new ResourceProperties();
        unsafePathProperties.setScenes(List.of(localScene("admin.logo", "../logo")));

        assertThatThrownBy(unsafePathProperties::validate).isInstanceOf(IllegalStateException.class).hasMessageContaining("path 配置非法");
    }

    @Test
    void validateRejectsInvalidServeModeCombinations() {
        ResourceProperties localPresignedProperties = new ResourceProperties();
        ResourceSceneConfig localPresigned = localScene("admin.file", "files");
        localPresigned.setServeMode(ServeModeEnum.PRESIGNED);
        localPresignedProperties.setScenes(List.of(localPresigned));

        assertThatThrownBy(localPresignedProperties::validate).isInstanceOf(IllegalStateException.class).hasMessageContaining("PRESIGNED 不能与 type=LOCAL");

        ResourceProperties ossProperties = new ResourceProperties();
        ResourceSceneConfig ossScene = new ResourceSceneConfig();
        ossScene.setCode("admin.oss");
        ossScene.setType(StorageTypeEnum.OSS);
        ossScene.setServeMode(ServeModeEnum.PRESIGNED);
        ossScene.setBucket("admin-files");
        ossScene.setExpire(0L);
        ossProperties.setScenes(List.of(ossScene));

        assertThatThrownBy(ossProperties::validate).isInstanceOf(IllegalStateException.class).hasMessageContaining("expire 无效");
    }

    private static ResourceSceneConfig localScene(String code, String path) {
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode(code);
        scene.setType(StorageTypeEnum.LOCAL);
        scene.setServeMode(ServeModeEnum.DIRECT);
        scene.setPath(path);
        scene.setBaseUrl("http://127.0.0.1/static/" + code);
        return scene;
    }
}
