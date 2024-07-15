package com.sz.admin.system.pojo.vo.sysdatascope;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SysDataScopeVO
 * @Author sz
 * @Date 2024/6/27 13:26
 * @Version 1.0
 */
@Data
@Schema(description = "SysDataScope返回vo")
public class SysDataScopeVO {

    {
        deptOptions = new ArrayList<>();
        userOptions = new ArrayList<>();
    }

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "关联类型，data_scope_relation_type")
    private String relationTypeCd;

    @Schema(description = "关联表id")

    private Long relationId;

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "关联部门")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> deptOptions;

    @Schema(description = "关联用户")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> userOptions;

}
