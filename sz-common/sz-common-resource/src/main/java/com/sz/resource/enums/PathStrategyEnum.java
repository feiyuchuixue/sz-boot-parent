package com.sz.resource.enums;

/**
 * 存储路径分层策略
 *
 * <p>
 * 控制上传文件在基础前缀下的子目录结构；LOCAL 的基础前缀为场景 path，OSS 的基础前缀为场景 bucket。日期分层取应用进程
 * 当前本地日期，格式为 yyyyMMdd。以 LOCAL 为例，影响：
 * <ul>
 * <li>物理存储路径：{@code root}/{@code path}/{subDirs}/{filename}</li>
 * <li>objectKey：{@code path}/{subDirs}/{filename}（不含 root 前缀）</li>
 * </ul>
 *
 * <p>
 * 示例（假设 path=avatars/, root=./data, pathSegments=["1"], date=20260403,
 * filename=abc.png）：
 * 
 * <pre>
 * FLAT      → objectKey: avatars/abc.png
 * DATE      → objectKey: avatars/20260403/abc.png
 * BIZ       → objectKey: avatars/1/abc.png
 * BIZ_DATE  → objectKey: avatars/1/20260403/abc.png
 * </pre>
 *
 * <p>
 * <b>注意</b>：枚举名中的 {@code BIZ} 表示"业务路径分段（pathSegments）"， 即 {@code upload()}
 * 最后一个可变参数 {@code pathSegments}， 与用于文件命名的 {@code namingKey} 参数无关。
 */
public enum PathStrategyEnum {

    /**
     * 平铺存储。 不增加子目录，objectKey 形如 basePath/filename。
     */
    FLAT,

    /**
     * 按日期分层。 使用应用进程当前本地日期增加 yyyyMMdd 子目录，objectKey 形如 basePath/yyyyMMdd/filename。
     */
    DATE,

    /**
     * 按业务路径分段分层。 pathSegments 中每个非空白元素依次成为一级子目录，不加日期；BIZ 指 pathSegments，与用于文件命名的
     * namingKey 无关。
     */
    BIZ,

    /**
     * 先按业务路径分段，再按日期分层。 依次加入 pathSegments 中的非空白元素和应用进程当前本地日期 yyyyMMdd 子目录；BIZ 指
     * pathSegments，与用于文件命名的 namingKey 无关。
     */
    BIZ_DATE,
}
