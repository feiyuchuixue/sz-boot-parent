package com.sz.mysql;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sz.data-scope")
public class DataScopeProperties {

    @Schema(description = "数据权限开关")
    private boolean enabled = true;

    @Schema(description = "最小查询单位")
    private String logicMinUnit = "user";

    @Schema(description = "是否允许查看管理员创建的数据")
    private boolean allowAdminView = false;

}
