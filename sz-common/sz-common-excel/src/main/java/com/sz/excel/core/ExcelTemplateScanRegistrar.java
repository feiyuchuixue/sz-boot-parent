package com.sz.excel.core;

import com.sz.excel.annotation.EnableExcelTemplateScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link EnableExcelTemplateScan} 的 BeanDefinition 注册器。
 * <p>
 * 由 {@code @Import} 触发，读取 {@link EnableExcelTemplateScan#basePackages()} 的值，
 * 将其作为构造参数注册 {@link ExcelTemplateScanRegistry} Bean。
 * </p>
 *
 * @author sz
 * @since 2026/04/09
 */
public class ExcelTemplateScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableExcelTemplateScan.class.getName()));

        if (attributes == null) {
            return;
        }

        String[] basePackages = attributes.getStringArray("basePackages");
        if (basePackages.length == 0) {
            return;
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ExcelTemplateScanRegistry.class).addConstructorArgValue(basePackages);

        registry.registerBeanDefinition(ExcelTemplateScanRegistry.BEAN_NAME, builder.getBeanDefinition());
    }
}
