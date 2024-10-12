package com.sz.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName StreamUtils
 * @Author sz
 * @Date 2023/12/27 8:39
 * @Version 1.0
 */
public class StreamUtils {

    /**
     * 将Collection转化为map(value类型与collection的泛型不同)<br>
     * <B>{@code Collection<E> -----> Map<K,V>  }</B>
     *
     * @param collection
     *            需要转化的集合
     * @param key
     *            E类型转化为K类型的lambda方法
     * @param value
     *            E类型转化为V类型的lambda方法
     * @param <E>
     *            collection中的泛型
     * @param <K>
     *            map中的key类型
     * @param <V>
     *            map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        if (collection == null || collection.size() == 0) {
            return new HashMap();
        }
        return collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(key, value, (l, r) -> l));
    }

    public static String listToStr(Collection<String> collection) {
        if (collection == null || collection.size() == 0) {
            return "";
        }
        String collect = collection.stream().collect(Collectors.joining(","));
        return collect;
    }

}
