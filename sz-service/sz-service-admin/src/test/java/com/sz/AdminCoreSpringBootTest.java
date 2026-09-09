package com.sz;

import com.sz.resource.config.ResourceAutoConfiguration;
import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.spi.ResourceSceneProvider;
import com.sz.resource.spi.ResourceSecurityPolicyProvider;
import com.sz.security.config.SaTokenConfig;
import com.sz.security.core.CorsProperties;
import com.sz.security.core.exception.SaExceptionHandler;
import com.sz.security.pojo.WhitelistProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AdminCoreSpringBootTest.TestBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.config.name=admin-core-spring-boot-test", "sz.resource.root=./data", "sz.resource.scenes[0].code=admin.avatar",
        "sz.resource.scenes[0].type=LOCAL", "sz.resource.scenes[0].serve-mode=DIRECT", "sz.resource.scenes[0].path=avatars",
        "sz.resource.scenes[0].base-url=http://127.0.0.1/static/avatars", "router.whitelist[0]=/captcha/**", "router.whitelist[1]=/auth/login",
        "sz.cors.allowed-origins[0]=http://127.0.0.1:3000"})
class AdminCoreSpringBootTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResourceProperties resourceProperties;

    @Autowired
    private ResourceSceneProvider resourceSceneProvider;

    @Autowired
    private ResourceSecurityPolicyProvider resourceSecurityPolicyProvider;

    @Autowired
    private WhitelistProperties whitelistProperties;

    @Autowired
    private CorsProperties corsProperties;

    @Test
    void coreSpringBootContextLoadsResourceAndSecurityInfrastructure() {
        assertThat(applicationContext.getBean(SaExceptionHandler.class)).isNotNull();
        assertThat(resourceSceneProvider).isNotNull();
        assertThat(resourceSecurityPolicyProvider).isNotNull();
    }

    @Test
    void resourcePropertiesAreBoundAndValidatedBySpringBoot() {
        ResourceSceneConfig scene = resourceProperties.getScene("admin.avatar");

        assertThat(scene.getCode()).isEqualTo("admin.avatar");
        assertThat(scene.getType()).isEqualTo(StorageTypeEnum.LOCAL);
        assertThat(scene.getServeMode()).isEqualTo(ServeModeEnum.DIRECT);
        assertThat(scene.getPath()).isEqualTo("avatars");
        assertThat(scene.getBaseUrl()).isEqualTo("http://127.0.0.1/static/avatars");
    }

    @Test
    void securityPropertiesAreBoundBySpringBoot() {
        assertThat(whitelistProperties.getWhitelist()).containsExactly("/captcha/**", "/auth/login");
        assertThat(corsProperties.getAllowedOrigins()).containsExactly("http://127.0.0.1:3000");
    }

    @Test
    void saTokenInterceptorRegistrationToleratesMissingWhitelist() {
        WhitelistProperties properties = new WhitelistProperties();
        SaTokenConfig config = new SaTokenConfig(properties);

        assertThatCode(() -> config.addInterceptors(new InterceptorRegistry())).doesNotThrowAnyException();
    }

    @Test
    void adminApplicationKeepsSpringBootEntrypointAnnotations() {
        assertThat(AdminApplication.class.getAnnotation(SpringBootApplication.class)).isNotNull();
        assertThat(AdminApplication.class.getAnnotation(EnableAspectJAutoProxy.class)).isNotNull();
    }

    @SpringBootConfiguration
    @EnableConfigurationProperties({ResourceProperties.class, WhitelistProperties.class, CorsProperties.class})
    @Import({ResourceAutoConfiguration.class, SaExceptionHandler.class})
    static class TestBootApplication {
    }
}
