package com.sz.www.sysregion.pojo.po;

import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import com.sz.mysql.EntityChangeListener;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
* <p>
* 系统行政区（字典）表
* </p>
*
* @author sz
* @since 2024-04-25
*/
@Data
@Table(value = "sys_region", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "系统行政区（字典）表")
public class SysRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description =" 行政区id")
    private Integer id;

    @Schema(description ="行政区名称")
    private String regionName;

    @Schema(description ="")
    private LocalDateTime createTime;

    @Schema(description ="")
    private LocalDateTime updateTime;

    @Schema(description ="删除与否")
    private String delFlag;

}