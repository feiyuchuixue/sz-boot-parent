package com.sz.generator.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 代码生成业务表
 * </p>
 *
 * @author sz
 * @since 2023-11-27
 */
@Data
@Table(value = "generator_table", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
public class GeneratorTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long tableId;

    /**
     * 表名称
     * <p>
     * private String tableName;
     * <p>
     * /** 表描述
     */
    private String tableName;

    private String tableComment;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * camel实体类名称
     */
    private String camelClassName;

    /**
     * 使用的模版
     */
    private String tplCategory;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成业务名
     */
    private String businessName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成作者名
     */
    private String functionAuthor;

    /**
     * 生成方式(0 zip压缩包；1 自定义路径)
     */
    private String type;

    /**
     * 其他参数
     */
    private String options;

    /**
     * 上级菜单id
     */
    private String parentMenuId;

    /**
     * 生成路径
     */
    private String path;

    /**
     * api生成路径
     */

    private String pathApi;

    /**
     * web生成路径
     */
    private String pathWeb;

    private Long createId;

    private Long updateId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 是否自动创建菜单路由（1 是）
     */
    private String menuInitType;

    /**
     * 是否自动创建按钮权限 (1 是)
     */
    private String btnPermissionType;

    /**
     * 是否支持导入(1 是)
     */
    private String hasImport;

    /**
     * 是否支持导出(1 是)
     */
    private String hasExport;

    /**
     * 生成类型
     * <p>
     * all 全部 server 后端 service 接口
     */
    private String generateType;

    // 是否自动填充(1 是)
    private String isAutofill;

}
