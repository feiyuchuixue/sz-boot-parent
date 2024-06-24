package com.sz.admin.teacher.pojo.po;

import com.mybatisflex.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import com.sz.mysql.EntityChangeListener;
/**
* <p>
* 教师表
* </p>
*
* @author sz-admin
* @since 2024-06-24
*/
@Data
@Table(value = "teacher", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "教师表")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="")
    private String name;

    @Schema(description ="")
    private Long createId;

    @Schema(description ="")
    private Integer areaId;

    @Schema(description ="")
    private Integer age;

}