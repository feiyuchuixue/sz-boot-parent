package com.sz.core.common.service;

import com.sz.core.common.entity.DictVO;

import java.util.List;
import java.util.Map;

/**
 * 通用字典获取
 *
 * @ClassName DictService
 * @Author sz
 * @Date 2023/12/26 16:38
 * @Version 1.0
 */
public interface DictService {

    /**
     * 分隔符
     */
    String SEPARATOR = ",";

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType
     *            字典类型
     * @param dictValue
     *            字典值
     * @return 字典标签
     */
    default String getDictLabel(String dictType, String dictValue) {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType
     *            字典类型
     * @param dictLabel
     *            字典标签
     * @return 字典值
     */
    default String getDictValue(String dictType, String dictLabel) {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType
     *            字典类型
     * @param dictValue
     *            字典值
     * @param separator
     *            分隔符
     * @return 字典标签
     */
    String getDictLabel(String dictType, String dictValue, String separator);

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType
     *            字典类型
     * @param dictLabel
     *            字典标签
     * @param separator
     *            分隔符
     * @return 字典值
     */
    String getDictValue(String dictType, String dictLabel, String separator);

    /**
     * 获取字典下所有的字典值与标签
     *
     * @param dictType
     *            字典类型
     * @return dictValue为key，dictLabel为值组成的Map
     */
    Map<String, String> getAllDictByType(String dictType);

    /**
     * 获取所有字典
     *
     * @return
     */
    Map<String, List<DictVO>> getAllDict();

}
