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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * spring工具类 方便在（非spring ioc）非spring管理环境中获取bean
 *
 * @author: sz
 * @date: 2021/9/15 13:37
 * @description: SpringBootApplicationContextUtil
 *               通过ApplicationContext上下文对象自定义获取bean
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
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     *
     * @param invoker
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 注册bean
     *
     * @param beanName
     *            注册的bean名称
     * @param clazz
     *            类型
     * @param function
     *            bean定义
     * @return 注册的bean实例
     * @author sz
     * @date 2022/3/9 9:43
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
     * 注册bean
     *
     * @param beanName
     *            注册的bean名称
     * @param clazz
     *            类型
     * @param args
     *            构造参数
     * @param property
     *            bean属性集
     * @return 注册的bean实例
     * @author sz
     * @date 2022/3/9 9:47
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
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringApplicationContextUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextUtils.applicationContext = applicationContext;
    }

    public static String getActive() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 判断是否是本地环境
     * 
     * @return
     */
    public static boolean isLocalEnv() {
        List<String> localEnvArr = Arrays.asList(localEnv);
        if (localEnvArr.contains(getActive())) {
            return true;
        }
        return false;
    }

}
