package com.sz.oss;

/**
 * 文件命名方式
 * 
 * @ClassName FileNamingEnum
 * @Author sz
 * @Date 2024/11/14 10:52
 * @Version 1.0
 */
public enum FileNamingEnum {
    // 每个文件都将被分配一个唯一的 UUID，这有助于在系统中精确标识文件，避免任何命名冲突。
    UUID,
    // 优先使用文件的原始名称，以保持其直观性和易于识别。如果出现文件名冲突，将通过添加时间戳（时分秒.毫秒,eg:
    // 162615.587）来解决，确保每个文件名的唯一性。
    ORIGINAL;
}
