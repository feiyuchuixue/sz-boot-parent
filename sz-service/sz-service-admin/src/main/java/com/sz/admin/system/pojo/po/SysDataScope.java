package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.*;

import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
/**
* <p>
* 自定义数据权限
* </p>
*
* @author sz-admin
* @since 2024-06-27
*/
@Data
@Table(value = "sys_data_scope", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "自定义数据权限")
public class SysDataScope implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description ="ID")
    private Long id;

    @Schema(description ="关联类型，data_scope_relation_type")
    private String relationTypeCd;

    @Schema(description ="关联表id")
    private Integer relationId;

    @Schema(description ="关联部门")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> deptOptions;

    @Schema(description ="关联用户")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> userOptions;

    @Schema(description ="创建人")
    private Long createId;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

}