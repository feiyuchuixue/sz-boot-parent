package com.sz.core.util;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 集合工具类
 * 
 * @author sz
 * @since 2022/8/26 9:09
 */
public class CollectorUtils {

    private CollectorUtils() {
        throw new IllegalStateException("CollectorUtils class Illegal");
    }

    /**
     * 返回两个列表的交集。
     * <p>
     * 此方法通过流操作，过滤出在两个列表中都存在的元素。适用于需要找出两个集合共同元素的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list1 = [1, 2, 3]
     * list2 = [2, 3]
     * 结果: [2, 3]
     * </pre>
     * </p>
     *
     * @param <T>
     *            列表中元素的类型
     * @param list1
     *            第一个列表
     * @param list2
     *            第二个列表
     * @return 包含两个列表中共同元素的列表
     */
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        return list1.stream().filter(list2::contains).collect(toList());
    }

    /**
     * 返回两个列表的差集，即在第一个列表中存在但在第二个列表中不存在的元素。
     * <p>
     * 此方法通过流操作，过滤出在第一个列表中但不在第二个列表中的元素。适用于需要找出一个列表中独有元素的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list1 = [1, 2, 3]
     * list2 = [2, 3]
     * 结果: [1]
     * </pre>
     * </p>
     *
     * @param <T>
     *            列表中元素的类型
     * @param list1
     *            第一个列表
     * @param list2
     *            第二个列表
     * @return 只包含在第一个列表中但不在第二个列表中的元素的列表
     */
    public static <T> List<T> reduce(List<T> list1, List<T> list2) {
        return list1.stream().filter(item -> !list2.contains(item)).collect(toList());
    }

    /**
     * 返回两个列表的并集（未去重），即将两个列表的元素合并。
     * <p>
     * 此方法通过流操作，将两个列表的元素合并为一个新列表，保留重复元素。适用于需要将两个集合的元素合并在一起的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list1 = [1, 2, 3]
     * list2 = [2, 3]
     * 结果: [1, 2, 3, 2, 3]
     * </pre>
     * </p>
     *
     * @param <T>
     *            列表中元素的类型
     * @param list1
     *            第一个列表
     * @param list2
     *            第二个列表
     * @return 合并后的列表，包含两个列表中的所有元素，未去重
     */
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        List<T> listAll = new ArrayList<>(list1); // 直接用list1初始化，避免冗余的 parallelStream
        listAll.addAll(list2); // 合并list2到listAll
        return listAll;
    }

    /**
     * 返回两个列表的并集（去重），即将两个列表的元素合并并移除重复的元素。
     * <p>
     * 此方法通过流操作，将两个列表的元素合并，并使用 `Set` 来去除重复的元素。适用于需要将两个集合合并且确保元素唯一的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list1 = [1, 2, 3]
     * list2 = [2, 3, 4]
     * 结果: [1, 2, 3, 4]
     * </pre>
     * </p>
     *
     * @param <T>
     *            列表中元素的类型
     * @param list1
     *            第一个列表
     * @param list2
     *            第二个列表
     * @return 合并后的列表，包含两个列表中的所有唯一元素
     */
    public static <T> List<T> unionDistinct(List<T> list1, List<T> list2) {
        List<T> listAll = union(list1, list2);
        return listAll.stream().distinct().collect(toList());
    }

    /**
     * 判断两个列表在排序后是否相同，比较它们的元素及顺序。
     * <p>
     * 此方法对两个列表进行排序，排序规则是根据每个元素的 `hashCode` 排序，然后比较排序后的两个列表是否相同。
     * 适用于需要判断两个列表内容是否相同，且顺序一致的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list1 = ["apple", "banana", "cherry"]
     * list2 = ["banana", "cherry", "apple"]
     * 结果: true
     * </pre>
     * </p>
     *
     * @param list1
     *            第一个列表
     * @param list2
     *            第二个列表
     * @return 如果两个列表排序后相同，则返回 `true`；否则返回 `false`
     */
    public static boolean listEquals(List<String> list1, List<String> list2) {
        list1.sort(Comparator.comparing(String::hashCode));
        list2.sort(Comparator.comparing(String::hashCode));
        return list1.toString().equals(list2.toString());
    }

    /**
     * 将列表转换为逗号分隔的字符串。
     * <p>
     * 此方法将列表中的每个元素转换为字符串，并用逗号将它们连接起来，适用于需要将列表元素合并为一个字符串的场景。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * list = ["a", "b", "c"]
     * 结果: "a,b,c"
     * </pre>
     * </p>
     *
     * @param <T>
     *            列表中元素的类型
     * @param list
     *            需要转换的列表
     * @return 以逗号分隔的字符串，包含列表中的所有元素
     */
    public static <T> String listToStr(List<T> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 根据 map 的键进行 ASCII 顺序排序。
     * <p>
     * 此方法将传入的 `map` 按照键的 ASCII 值进行升序排序，并返回排序后的新 `map`。 如果 `map` 为 `null` 或空，方法将返回
     * `null`。
     * </p>
     * <p>
     * 例如：
     * 
     * <pre>
     * map = {"b":1, "d":2, "a":3, "b2":4, "c":5}
     * 结果: {"a":3, "b":1, "b2":4, "c":5, "d":2}
     * </pre>
     * </p>
     *
     * @param map
     *            待排序的 `Map`，其键将根据 ASCII 顺序排序
     * @return 排序后的 `Map`，如果输入 `map` 为 `null` 或空，则返回 `null`
     */
    public static Map<String, Object> sortMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        return new TreeMap<>(map);
    }

}
