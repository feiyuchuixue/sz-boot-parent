package com.sz.excel.enums;

/**
 * Excel 枚举字段预置映射类型。
 * <p>
 * 设计目标：避免在 Excel 导入导出时依赖“第一个字段是 code、第二个字段是 desc/message”这类隐式约定， 改为由字段显式声明映射方式。
 * </p>
 * <p>
 * 当前仅保留两个最小且清晰的预置：
 * </p>
 * <ul>
 * <li>{@link #YES_NO}：专用于 {@code YesNoEnum}，固定按 {@code desc} 导出与导入。</li>
 * <li>{@link #CUSTOM}：用于所有业务自定义枚举，必须配合 {@code writeField/readField} 明确声明。</li>
 * </ul>
 */
public enum ExcelEnumPreset {

    /**
     * 专用于 YesNoEnum。
     * <p>
     * 固定规则：
     * </p>
     * <ul>
     * <li>导出：读取枚举属性 {@code desc}，例如导出为“是/否”</li>
     * <li>导入：按枚举属性 {@code desc} 回写</li>
     * </ul>
     */
    YES_NO,

    /**
     * 自定义枚举映射。
     * <p>
     * 适用于除 YesNoEnum 以外的所有业务枚举。使用该类型时，字段上必须显式指定：
     * </p>
     * <ul>
     * <li>{@code writeField}：导出时从枚举对象读取哪个属性写入 Excel</li>
     * <li>{@code readField}：导入时用 Excel 单元格文本去匹配枚举对象的哪个属性</li>
     * </ul>
     */
    CUSTOM
}
