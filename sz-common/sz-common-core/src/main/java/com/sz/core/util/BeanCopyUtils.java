package com.sz.core.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * bean copy
 *
 * @author sz
 * @since 2021/9/1 22:04
 */
public class BeanCopyUtils {

    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 设置严格模式
    }

    private BeanCopyUtils() {
        throw new IllegalStateException("BeanCopyUtils class Illegal");
    }

    /**
     * ！！！将在下个版本移除！！！ 已弃用的方法，用于复制对象属性。
     * <p>
     * 此方法使用 Spring 的 `BeanUtils.copyProperties` 来复制源对象的属性到目标对象。 建议使用 `ModelMapper`
     * 实现，提供更灵活和强大的映射功能。
     * </p>
     *
     * @param <T>
     *            源对象的类型
     * @param <M>
     *            目标对象的类型
     * @param source
     *            源对象
     * @param target
     *            目标对象
     * @deprecated 此方法将在后续版本中弃用，请使用 `ModelMapper` 实现。
     * @since 2021-09-01
     */
    @Deprecated(since = "2021-09-01", forRemoval = true)
    public static <T, M> void springCopy(T source, M target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * ！！！将在下个版本移除！！！ 已弃用的方法，用于将源对象转换为指定类型的目标对象。
     * <p>
     * 此方法将在后续版本中弃用，建议使用 `ModelMapper` 实现以获得更灵活和强大的映射功能。
     * </p>
     *
     * @param <T>
     *            目标对象的类型
     * @param source
     *            源对象
     * @param clazz
     *            目标对象的类类型
     * @return 转换后的目标对象实例
     * @deprecated 使用 `ModelMapper` 替代此方法，以支持更加灵活的对象转换。
     */
    @Deprecated(since = "2021-09-01", forRemoval = true)
    public static <T> T springCopy(Object source, Class<T> clazz) {
        T target = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 使用 ModelMapper 进行对象属性复制，采用严格匹配模式。
     * <p>
     * 只有属性名称和类型完全一致的情况下，才会进行属性复制。此方法适用于需要精确映射的场景。
     * </p>
     *
     * @param <T>
     *            目标对象的类型
     * @param source
     *            源对象
     * @param clazz
     *            目标对象的类类型
     * @return 转换后的目标对象实例
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        return modelMapper.map(source, clazz);
    }

    public static <T, M> M copy(T source, M target) {
        modelMapper.map(source, target);
        return target;
    }

    /**
     * 使用 ModelMapper 进行对象属性复制，严格匹配且不忽略 `null` 值。
     * <p>
     * 此方法在属性名称和类型完全一致时进行复制，即使属性值为 `null` 也会进行覆盖。
     * </p>
     *
     * @param <T>
     *            源对象的类型
     * @param <M>
     *            目标对象的类型
     * @param source
     *            源对象
     * @param target
     *            目标对象
     * @return 无返回值，目标对象的属性会被源对象对应的属性值覆盖
     */
    public static <T, M> M copyNotIgnoreNull(T source, M target) {
        modelMapper.getConfiguration().setSkipNullEnabled(false); // 不跳过null值
        modelMapper.map(source, target);
        return target;
    }

    /**
     * 使用 ModelMapper 将源列表中的每个对象复制为目标类型的对象列表。
     * <p>
     * 此方法通过流操作，将源列表中的每个对象映射为目标类型的新实例。适用于需要批量转换对象类型的场景。
     * </p>
     *
     * @param <Source>
     *            源列表中对象的类型
     * @param <Target>
     *            目标列表中对象的类型
     * @param sourceList
     *            源对象列表，包含需要转换的对象
     * @param targetClass
     *            目标对象的类类型，用于指定转换的目标类型
     * @return 转换后的目标对象列表，每个对象都是 `targetClass` 类型的新实例
     */
    public static <Source, Target> List<Target> copyList(List<Source> sourceList, Class<Target> targetClass) {
        return sourceList.stream().map(source -> modelMapper.map(source, targetClass)).collect(Collectors.toList());
    }

}
