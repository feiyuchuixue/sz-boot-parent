package com.sz.excel.annotation;

import com.sz.excel.enums.ExcelEnumPreset;

import java.lang.annotation.*;

/**
 * Excel 枚举字段映射注解。
 * <p>
 * 用于声明“Java 枚举字段”在 Excel 导入和导出时的双向映射规则。
 * </p>
 * <p>
 * 使用该注解后，Excel 框架将不再依赖“枚举第一个属性是 code、第二个属性是 desc/message”这类隐式约定， 而是完全按字段上声明的
 * preset / writeField / readField 处理。
 * </p>
 * <p>
 * 典型使用场景如下：
 * </p>
 * <ul>
 * <li><b>场景1：YesNoEnum</b><br/>
 * 适用于系统通用的 {@code YesNoEnum}。推荐写法：<br/>
 * {@code @ExcelEnumFormat(preset = ExcelEnumPreset.YES_NO)}<br/>
 * 含义：导出显示“是/否”，导入也按“是/否”匹配。</li>
 *
 * <li><b>场景2：业务枚举按 description 导出导入</b><br/>
 * 例如枚举有 {@code code + description} 两个属性，希望 Excel 中展示/填写中文描述：<br/>
 * {@code @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "description", readField = "description")}</li>
 *
 * <li><b>场景3：业务枚举导出按 message，导入也按 message</b><br/>
 * 例如枚举有 {@code code + message}：<br/>
 * {@code @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "message", readField = "message")}</li>
 *
 * <li><b>场景4：属性名不统一</b><br/>
 * 如果业务枚举展示字段不是 {@code desc/message/description}，而是自定义字段， 直接填写该属性名即可，例如：<br/>
 * {@code @ExcelEnumFormat(preset = ExcelEnumPreset.CUSTOM, writeField = "displayName", readField = "displayName")}</li>
 * </ul>
 * <p>
 * 字段含义说明：
 * </p>
 * <ul>
 * <li>{@code preset}：选择预置类型。目前仅支持 {@code YES_NO} 与 {@code CUSTOM}</li>
 * <li>{@code writeField}：导出时，从枚举对象读取哪个属性作为 Excel 单元格文本</li>
 * <li>{@code readField}：导入时，用 Excel 单元格文本去匹配枚举对象的哪个属性</li>
 * </ul>
 * <p>
 * 约束说明：
 * </p>
 * <ul>
 * <li>当 {@code preset = YES_NO} 时，不需要填写 {@code writeField/readField}</li>
 * <li>当 {@code preset = YES_NO} 时，字段类型必须是 {@code YesNoEnum}</li>
 * <li>当 {@code preset = CUSTOM} 时，必须同时填写 {@code writeField/readField}</li>
 * <li>若字段同时标注了 {@link DictFormat}，则优先按 {@link DictFormat} 处理，不走枚举映射</li>
 * </ul>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelEnumFormat {

    /**
     * 枚举映射预置类型。
     */
    ExcelEnumPreset preset();

    /**
     * 导出时取值的枚举属性名。
     * <p>
     * 仅在 {@code preset = CUSTOM}
     * 时生效，例如：{@code desc}、{@code description}、{@code message}。
     * </p>
     */
    String writeField() default "";

    /**
     * 导入时匹配的枚举属性名。
     * <p>
     * 仅在 {@code preset = CUSTOM}
     * 时生效，例如：{@code desc}、{@code description}、{@code message}。
     * </p>
     */
    String readField() default "";

    /**
     * 导入枚举匹配时是否忽略大小写。
     */
    boolean ignoreCase() default true;

    /**
     * 当按 {@code readField} 未匹配成功时，是否继续尝试按 {@code enum.name()} 匹配。
     */
    boolean fallbackToName() default true;
}
