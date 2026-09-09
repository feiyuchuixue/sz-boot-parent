package com.sz.excel.core;

import com.sz.excel.annotation.EnableExcelTemplateScan;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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

        if (registry.containsBeanDefinition(ExcelTemplateScanRegistry.BEAN_NAME)) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(ExcelTemplateScanRegistry.BEAN_NAME);
            String[] mergedBasePackages = mergeBasePackages(beanDefinition, basePackages);
            if (beanDefinition instanceof AbstractBeanDefinition abstractBeanDefinition) {
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                constructorArgumentValues.addGenericArgumentValue(mergedBasePackages);
                abstractBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
            } else {
                beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, mergedBasePackages);
            }
            return;
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ExcelTemplateScanRegistry.class).addConstructorArgValue(basePackages);
        registry.registerBeanDefinition(ExcelTemplateScanRegistry.BEAN_NAME, builder.getBeanDefinition());
    }

    private String[] mergeBasePackages(BeanDefinition beanDefinition, String[] basePackages) {
        Set<String> merged = new LinkedHashSet<>();
        ValueHolder valueHolder = beanDefinition.getConstructorArgumentValues().getIndexedArgumentValue(0, String[].class);
        if (valueHolder == null) {
            valueHolder = beanDefinition.getConstructorArgumentValues().getGenericArgumentValue(String[].class);
        }
        if (valueHolder != null && valueHolder.getValue() instanceof String[] existingBasePackages) {
            merged.addAll(Arrays.asList(existingBasePackages));
        }
        merged.addAll(Arrays.asList(basePackages));
        return merged.toArray(String[]::new);
    }
}
