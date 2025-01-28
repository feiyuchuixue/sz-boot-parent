package com.sz.core.util;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Spring 工具类：用于在非 Spring 管理的环境中获取 Bean，方便在非 Spring IoC 中使用。 提供获取 Bean、注册
 * Bean、环境配置等实用功能。
 */
@Component
public class SpringApplicationContextUtils implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ConfigurableListableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    private static final String[] localEnv = {"dev", "local"};

    private SpringApplicationContextUtils() {
        // 私有构造函数防止实例化
    }

    private static class Holder {

        private static final SpringApplicationContextUtils INSTANCE = new SpringApplicationContextUtils();
    }

    public static SpringApplicationContextUtils getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringApplicationContextUtils.getInstance().beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextUtils.getInstance().applicationContext = applicationContext;
    }

    public <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    public <T> T getBean(Class<T> clz) throws BeansException {
        return beanFactory.getBean(clz);
    }

    public boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    public <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    public String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public <T> T registerBean(String beanName, Class<T> clazz, Function<BeanDefinitionBuilder, AbstractBeanDefinition> function) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        AbstractBeanDefinition beanDefinition = function.apply(beanDefinitionBuilder);
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) this.beanFactory;
        if (isNotBlank(beanName) && !containsBean(beanName)) {
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            return getBean(beanName);
        } else {
            String name = BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, beanDefinitionRegistry);
            return getBean(name);
        }
    }

    public <T> T registerBean(String beanName, Class<T> clazz, List<Object> args, Map<String, Object> property) {
        return registerBean(beanName, clazz, beanDefinitionBuilder -> {
            if (!CollectionUtils.isEmpty(args)) {
                args.forEach(beanDefinitionBuilder::addConstructorArgValue);
            }
            if (!CollectionUtils.isEmpty(property)) {
                property.forEach(beanDefinitionBuilder::addPropertyValue);
            }
            return beanDefinitionBuilder.getBeanDefinition();
        });
    }

    public String getActive() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    public boolean isLocalEnv() {
        List<String> localEnvArr = Arrays.asList(localEnv);
        return localEnvArr.contains(getActive());
    }
}