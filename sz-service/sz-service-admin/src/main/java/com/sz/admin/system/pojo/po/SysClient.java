package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 系统授权表
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Data
@Table(value = "sys_client", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "系统授权表")
public class SysClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "客户端id")
    private String clientId;

    @Schema(description = "客户端key")
    private String clientKey;

    @Schema(description = "客户端秘钥")
    private String clientSecret;

    @Schema(description = "授权类型")
    private String grantTypeCd;

    @Schema(description = "设备类型")
    private String deviceTypeCd;

    @Schema(description = "token活跃超时时间")
    private Integer activeTimeout;

    @Schema(description = "token固定超时")
    private Integer timeout;

    @Schema(description = "状态")
    private String clientStatusCd;

    @Schema(description = "删除标志")
    private String delFlag;

    @Schema(description = "创建部门")
    private Integer createDept;

    @Schema(description = "创建者")
    private Long createId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private Long updateId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否锁定")
    private String isLock;

}