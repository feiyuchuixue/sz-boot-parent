package com.sz.resource.enums;

/**
 * 存储路径分层策略
 *
 * <p>
 * 控制上传文件在 {@code path}（基础目录）下的子目录结构，影响：
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
     * 平铺（默认）：所有文件直接存于 path 目录下，无子目录
     * 
     * <pre>
     * avatars / abc.png
     * </pre>
     */
    FLAT,

    /**
     * 按日期自动分片：框架自动在 path 下创建 yyyyMMdd 子目录
     * 
     * <pre>
     * avatars / 20260403 / abc.png
     * </pre>
     */
    DATE,

    /**
     * 仅按业务路径分段分层：{@code pathSegments} 中每个元素作为一级子目录，无日期后缀
     * 
     * <pre>
     * avatars/1/abc.png（pathSegments=["1"]）
     * </pre>
     * <p>
     * <b>注意</b>：{@code BIZ} 指的是 {@code pathSegments}，与 {@code namingKey} 无关
     */
    BIZ,

    /**
     * 先按业务路径分段、再按日期分层：{@code pathSegments} 子目录 + yyyyMMdd 子目录
     * 
     * <pre>
     * avatars/1/20260403/abc.png（pathSegments=["1"]）
     * </pre>
     * <p>
     * <b>注意</b>：{@code BIZ} 指的是 {@code pathSegments}，与 {@code namingKey} 无关
     */
    BIZ_DATE,
}
