package com.sz.core.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: sz
 * @date: 2021/9/1 22:04
 * @description: bean copy
 */
public class BeanCopyUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    private BeanCopyUtils() {
        throw new IllegalStateException("BeanCopyUtils class Illegal");
    }

    /**
     * 后续版本弃用，使用modelMapper实现，支持更加灵活
     *
     * @param source
     * @param target
     * @return void
     * @author sz
     * @date 2021-09-01 22:57:16
     */
    @Deprecated
    public static <T, M> void springCopy(T source, M target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 后续版本弃用，使用modelMapper实现，支持更加灵活
     *
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T springCopy(Object source, Class<T> clazz) {
        T target = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * modelMapper bean copy, 严格匹配模式 （属性名称与类型必须一致才进行复制）
     * 
     * @param source
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(source, clazz);
    }

    public static <T, M> M copy(T source, M target) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.map(source, target);
        return target;
    }

    /**
     * modelMapper bean copy, 严格匹配，不忽略null值
     * 
     * @param source
     * @param target
     * @return
     * @param <T>
     * @param <M>
     */
    public static <T, M> M copyNotIgnoreNull(T source, M target) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setSkipNullEnabled(false); // 不跳过null值
        mapper.map(source, target);
        return target;
    }

    /**
     * list 复制
     *
     * @param sourceList
     * @param targetClass
     * @param <Source>
     * @param <Target>
     * @return
     */
    public static <Source, Target> List<Target> copyList(List<Source> sourceList, Class<Target> targetClass) {
        return sourceList.stream().map(source -> modelMapper.map(source, targetClass)).collect(Collectors.toList());
    }

}
