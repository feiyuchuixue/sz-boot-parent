package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * <p>
 * 部门领导人表
 * </p>
 *
 * @author sz
 * @since 2024-03-26
 */
@Data
@Table(value = "sys_dept_leader")
@Schema(description = "部门领导人表")
public class SysDeptLeader implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "dept_id")
    private Long deptId;

    @Schema(description = "sys_user_id")
    private Long leaderId;

}
