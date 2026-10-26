package com.sz.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据权限全局配置。配置只控制数据权限方言的行为；业务查询仍需显式进入数据权限会话，并具备相应用户权限和表字段。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.data-scope")
public class DataScopeProperties {

    /**
     * 是否启用数据权限。关闭后方言不向查询注入数据范围条件，登录用户构建过程也不加载角色数据范围；业务查询仍需通过 DataScopeSession
     * 等方式进入数据权限处理流程，单独开启本开关不会使所有查询自动过滤。
     */
    @Schema(description = "是否启用数据权限；关闭后不注入数据范围条件，开启后业务查询仍需显式进入数据权限处理流程。")
    private boolean enabled = true;

    /**
     * 数据权限部门范围的最小过滤单位。当前消费者仅对精确字符串 user 采用 create_id 和用户部门关系过滤；其他任何值，包括
     * dept、null、空串和未知字符串，都会进入 dept_scope 部门范围分支。本字段没有运行时枚举校验，建议仅配置 user 或 dept，
     * 并确保业务表具有对应字段。
     *
     * @schemaExample user | 按数据创建人及其部门关系过滤
     * @schemaExample dept | 按业务表 dept_scope 部门范围过滤
     */
    @Schema(description = "部门范围的最小过滤单位；精确值 user 使用 create_id，其他值进入 dept_scope 分支，建议仅配置 user 或 dept。")
    private String logicMinUnit = "user";

    /**
     * 部门范围过滤时是否允许查看管理员创建的数据。存在部门过滤条件时，true 会把管理员数据作为可见分支加入，false 会显式排除
     * 管理员数据；仅按用户列表或本人 create_id 过滤、所需字段缺失而未形成部门过滤时，本字段不生效。当前数据范围为 ALL 时会在
     * 构造这些过滤条件前直接放行，本字段同样不生效。
     */
    @Schema(description = "部门范围过滤时是否允许查看管理员创建的数据；仅按用户或本人过滤时不生效。")
    private boolean allowAdminView = false;

}
