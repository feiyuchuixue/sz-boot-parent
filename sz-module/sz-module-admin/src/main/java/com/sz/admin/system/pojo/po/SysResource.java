package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源实体主表
 * <p>
 * 记录通过统一资源管理体系（sz-common-resource）上传的每一个文件资源。
 * </p>
 */
@Data
@Table(value = "sys_resource", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "资源实体主表")
public class SysResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Schema(description = "主键（雪花ID）")
    private Long id;

    @Schema(description = "场景编码，如 sso.provider.logo")
    private String sceneCode;

    @Schema(description = "存储键（相对路径/objectName），如 providers/github.svg")
    private String objectKey;

    @Schema(description = "原始文件名")
    private String originName;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "MIME 类型")
    private String contentType;

    @Schema(description = "存储类型：LOCAL / OSS")
    private String storageType;

    @Schema(description = "文件 ETag（MD5 或存储端返回的标识）")
    private String eTag;

    @Schema(description = "业务标识，如 providerKey")
    private String bizKey;

    @Column(isLogicDelete = true)
    @Schema(description = "逻辑删除：T=已删除 F=正常")
    private String delFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private Long updateId;
}
