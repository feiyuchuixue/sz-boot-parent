package com.sz.resource.config;

import com.sz.resource.driver.LocalResourceStorageDriver;
import com.sz.resource.driver.OssResourceStorageDriver;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.service.ResourceService;
import com.sz.resource.spi.YmlResourceSceneProvider;
import com.sz.resource.spi.YmlSecurityPolicyProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    @Test
    void defaultSceneDoesNotRequirePublicUrl() {
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode("business.file");
        scene.setPath("files");
        ResourceProperties properties = new ResourceProperties();
        properties.setScenes(List.of(scene));

        assertThatCode(properties::validate).doesNotThrowAnyException();
        assertThat(resourceService(properties).resolveUrl("business.file", "files/report.pdf")).isNull();
    }

    @Test
    void defaultSceneDoesNotExposeUrlEvenWhenBaseUrlIsConfigured() {
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode("business.file");
        scene.setPath("files");
        scene.setBaseUrl("https://cdn.example/files");
        ResourceProperties properties = new ResourceProperties();
        properties.setScenes(List.of(scene));
        properties.validate();

        assertThat(resourceService(properties).resolveUrl("business.file", "files/report.pdf")).isNull();
    }

    @Test
    void protectedModeBindsWithoutRequiringUrlOrExpiry() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
                "sz.resource.scenes[0].code", "business.file", "sz.resource.scenes[0].path", "files",
                "sz.resource.scenes[0].serve-mode", "PROTECTED", "sz.resource.scenes[0].expire", "0"));

        assertThatCode(() -> {
            ResourceProperties properties = new Binder(source).bind("sz.resource", Bindable.of(ResourceProperties.class)).get();
            properties.validate();
            assertThat(resourceService(properties).resolveUrl("business.file", "files/report.pdf")).isNull();
        }).doesNotThrowAnyException();
    }

    @Test
    void explicitDirectModeRequiresBaseUrlAndResolvesPublicUrl() {
        ResourceProperties properties = new ResourceProperties();
        ResourceSceneConfig scene = localScene("admin.avatar", "avatars");
        properties.setScenes(List.of(scene));
        properties.validate();

        assertThat(resourceService(properties).resolveUrl("admin.avatar", "avatars/user.png"))
                .isEqualTo("http://127.0.0.1/static/admin.avatar/user.png");

        scene.setBaseUrl(null);
        assertThatThrownBy(properties::validate).isInstanceOf(IllegalStateException.class).hasMessageContaining("base-url");
    }

    private static ResourceService resourceService(ResourceProperties properties) {
        return new ResourceService(properties, new LocalResourceStorageDriver(properties),
                new DefaultListableBeanFactory().getBeanProvider(OssResourceStorageDriver.class),
                new YmlResourceSceneProvider(properties), new YmlSecurityPolicyProvider(properties));
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
