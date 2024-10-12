package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author sz
 * @since 2023-11-23
 */

@Data
@Table(value = "sys_config", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "参数配置表")
public class SysConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "参数名")
    private String configName;

    @Schema(description = "参数key")
    private String configKey;

    @Schema(description = "参数value")
    private String configValue;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否锁定")
    private String isLock;

    private Long createId;

    private LocalDateTime createTime;

    private Long updateId;

    private LocalDateTime updateTime;
}
