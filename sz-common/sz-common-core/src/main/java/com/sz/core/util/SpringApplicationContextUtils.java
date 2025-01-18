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

/**
 * Spring 工具类：用于在非 Spring 管理的环境中获取 Bean，方便在非 Spring IoC 中使用。 提供获取 Bean、注册
 * Bean、环境配置等实用功能。
 *
 * @author sz
 * @since 2021/9/15
 */
@Component
public class SpringApplicationContextUtils implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    private final static String[] localEnv = {"dev", "local"};

    /**
     * 根据名称获取 Bean 对象。
     *
     * @param name
     *            Bean 名称
     * @param <T>
     *            Bean 类型
     * @return 对应的 Bean 对象
     * @throws BeansException
     *             如果 Bean 不存在或无法获取
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 根据类型获取 Bean 对象。
     *
     * @param clz
     *            Bean 类型
     * @param <T>
     *            Bean 类型
     * @return 对应的 Bean 对象
     * @throws BeansException
     *             如果 Bean 不存在或无法获取
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return beanFactory.getBean(clz);
    }

    /**
     * 检查 BeanFactory 中是否包含指定名称的 Bean。
     *
     * @param name
     *            Bean 名称
     * @return 如果包含则返回 true，否则返回 false
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断指定名称的 Bean 是否是单例。
     *
     * @param name
     *            Bean 名称
     * @return 如果是单例返回 true，否则返回 false
     * @throws NoSuchBeanDefinitionException
     *             如果没有找到对应的 Bean 定义
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * 获取指定名称的 Bean 类型。
     *
     * @param name
     *            Bean 名称
     * @return Bean 的类型
     * @throws NoSuchBeanDefinitionException
     *             如果没有找到对应的 Bean 定义
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 获取指定名称的 Bean 别名列表。
     *
     * @param name
     *            Bean 名称
     * @return Bean 的别名数组
     * @throws NoSuchBeanDefinitionException
     *             如果没有找到对应的 Bean 定义
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取 AOP 代理对象。
     *
     * @param invoker
     *            目标对象
     * @param <T>
     *            目标对象的类型
     * @return AOP 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前激活的环境配置。如果未配置，返回空数组。
     *
     * @return 当前激活的环境配置数组
     */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 注册 Bean 到 Spring 容器中。
     *
     * @param beanName
     *            注册的 Bean 名称
     * @param clazz
     *            Bean 类型
     * @param function
     *            定义 Bean 的函数
     * @param <T>
     *            Bean 类型
     * @return 注册的 Bean 实例
     */
    public static <T> T registerBean(String beanName, Class<T> clazz, Function<BeanDefinitionBuilder, AbstractBeanDefinition> function) {
        // 生成bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        // 执行bean处理函数
        AbstractBeanDefinition beanDefinition = function.apply(beanDefinitionBuilder);
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) SpringApplicationContextUtils.beanFactory;
        // 判断是否通过beanName注册
        if (StringUtils.isNotBlank(beanName) && !containsBean(beanName)) {
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
            return getBean(beanName);
        } else {
            // 非命名bean注册
            String name = BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, beanFactory);
            return getBean(name);
        }
    }

    /**
     * 注册带有构造参数和属性的 Bean。
     *
     * @param beanName
     *            注册的 Bean 名称
     * @param clazz
     *            Bean 类型
     * @param args
     *            构造参数列表
     * @param property
     *            Bean 属性集
     * @param <T>
     *            Bean 类型
     * @return 注册的 Bean 实例
     */
    public static <T> T registerBean(String beanName, Class<T> clazz, List<Object> args, Map<String, Object> property) {
        return registerBean(beanName, clazz, beanDefinitionBuilder -> {
            // 放入构造参数
            if (!CollectionUtils.isEmpty(args)) {
                args.forEach(beanDefinitionBuilder::addConstructorArgValue);
            }
            // 放入属性
            if (!CollectionUtils.isEmpty(property)) {
                property.forEach(beanDefinitionBuilder::addPropertyValue);
            }
            return beanDefinitionBuilder.getBeanDefinition();
        });
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringApplicationContextUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextUtils.applicationContext = applicationContext;
    }

    public static String getActive() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 判断是否是本地环境。
     *
     * @return 如果是本地环境返回 true，否则返回 false
     */
    public static boolean isLocalEnv() {
        List<String> localEnvArr = Arrays.asList(localEnv);
        return localEnvArr.contains(getActive());
    }

}
