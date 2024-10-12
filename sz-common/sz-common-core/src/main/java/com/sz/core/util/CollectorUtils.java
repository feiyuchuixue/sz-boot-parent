package com.sz.core.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author: sz
 * @date: 2022/8/26 9:09
 * @description: 集合tools
 */
public class CollectorUtils {

    private CollectorUtils() {
        throw new IllegalStateException("CollectorUtils class Illegal");
    }

    /**
     * 取两个list的交集 - list1 => [1,2,3] - list2 => [2,3] - 期望值: [2,3]
     *
     * @param list1
     * @param list2
     * @return List<T>
     */
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        return list1.stream().filter(item -> list2.contains(item)).collect(toList());
    }

    /**
     * 取两个list的差集 - list1 => [1,2,3] - list2 => [2,3] - 期望值: [1]
     *
     * @param list1
     * @param list2
     * @return List<T>
     */
    public static <T> List<T> reduce(List<T> list1, List<T> list2) {
        return list1.stream().filter(item -> !list2.contains(item)).collect(toList());
    }

    /**
     * 取两个list的并集（未去重） - list1 => [1,2,3] - list2 => [2,3] - 期望值: [1,2,3,2,3]
     *
     * @param list1
     * @param list2
     * @return List<T>
     */
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        List<T> listAll = list1.parallelStream().collect(toList());
        List<T> listAll2 = list2.parallelStream().collect(toList());
        listAll.addAll(listAll2);
        return listAll;
    }

    /**
     * 取两个list的并集（去重） - list1 => [1,2,3] - list2 => [2,3,4] - 期望值: [1,2,3,4]
     *
     * @param list1
     * @param list2
     * @return List<T>
     */
    public static <T> List<T> unionDistinct(List<T> list1, List<T> list2) {
        List<T> listAll = union(list1, list2);
        return listAll.stream().distinct().collect(toList());
    }

    /**
     * 判断两个list是否相同(排序后)
     *
     * @param list1
     * @param list2
     * @return boolean
     */
    public static boolean listEquals(List<String> list1, List<String> list2) {
        list1.sort(Comparator.comparing(String::hashCode));
        list2.sort(Comparator.comparing(String::hashCode));
        return list1.toString().equals(list2.toString());
    }

    /**
     * list转逗号分隔字符串 - list => ["a","b","c"] - 期望值: "a,b,c"
     *
     * @param list
     * @return String
     */
    public static <T> String listToStr(List<T> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 根据map key 进行排序 (针对 map->key 进行 ASCii 排序) - list => ["b","d","a","b2","c"] -
     * 期望值: ["a","b","b2","c","d"]
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        TreeMap sortMap = new TreeMap();
        sortMap.putAll(map);
        return sortMap;
    }

}
