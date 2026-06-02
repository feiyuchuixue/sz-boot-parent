package com.sz.excel.core;

import com.sz.excel.annotation.EnableExcelTemplateScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelTemplateScanRegistrarTest {

    @Test
    void registerBeanDefinitionsShouldMergeMultipleTemplateScanPackages() {
        SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ExcelTemplateScanRegistrar registrar = new ExcelTemplateScanRegistrar();

        registrar.registerBeanDefinitions(AnnotationMetadata.introspect(AdminTemplateScanConfiguration.class), registry);
        registrar.registerBeanDefinitions(AnnotationMetadata.introspect(OrderTemplateScanConfiguration.class), registry);

        BeanDefinition beanDefinition = registry.getBeanDefinition(ExcelTemplateScanRegistry.BEAN_NAME);
        ValueHolder valueHolder = beanDefinition.getConstructorArgumentValues().getGenericArgumentValue(String[].class);

        assertThat(valueHolder).isNotNull();
        assertThat((String[]) valueHolder.getValue()).containsExactly("com.sz.admin", "com.sz.order");
    }

    @EnableExcelTemplateScan(basePackages = "com.sz.admin")
    private static class AdminTemplateScanConfiguration {
    }

    @EnableExcelTemplateScan(basePackages = "com.sz.order")
    private static class OrderTemplateScanConfiguration {
    }
}
