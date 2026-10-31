package com.sz.generator.pojo.property;

import com.sz.generator.config.ConditionalOnGeneratorEnabled;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 代码生成器工程路径与导入默认配置。仅在 generator API 模块启用时注册；这些值用于初始化生成配置，用户随后可在生成器页面为
 * 单张表选择具体目标。
 *
 * @author sz
 * @since 2024/1/15 8:38
 */
@Data
@ConditionalOnGeneratorEnabled
@Component
@ConfigurationProperties(prefix = "sz.generator")
public class GeneratorProperties {

    /**
     * 代码生成目标路径。整个对象或其中字段缺省、为空白时，生成器会基于应用进程工作目录识别工程根目录，再选择推荐后端模块并 推导同级 sz-admin
     * 前端目录；显式路径作为导入表时的初始目标和路径选项，不替代后续页面中保存的表级目标路径。
     */
    private PathProperties path;

    /**
     * 导入数据库表时使用的全局代码信息。当前导入流程会直接读取本对象及 ignore-table-prefix，实际使用生成器时应完整配置； 缺少整个对象或缺少
     * ignore-table-prefix 会导致导入流程无法读取配置。
     */
    private GlobalProperties global;

    /**
     * 后端模块聚合目录名。该值原样传给 projectRoot.resolve，用于扫描后端模块，并在缺少显式 api 路径时参与拼接候选模块路径；
     * 精确空串会解析为项目根目录，纯空白会解析为名称含空白的子目录，null 没有保护并会导致路径解析失败。代码不会自动推断其他 聚合目录。
     */
    private String moduleName = "sz-module";

    /**
     * 未扫描到可推荐后端模块时使用的后端目标模块名。生成器以它构造回退模块代码、API 前缀和项目根目录下的目标路径；空白时 再使用代码内置的后端模块名。
     */
    private String serviceName = "sz-module-admin";

    @Data
    public static class GlobalProperties {

        /**
         * 导入数据库表时写入生成配置的作者名称，后续供代码模板使用。字段内容按配置原样保存，未做格式或非空校验。
         */
        private String author = "sz-admin";

        /**
         * 导入数据库表时写入生成配置的基础 Java 包名，也作为无法从目标模块源码识别包名时的回退。当前导入流程会原样保存本值， 不在配置绑定阶段校验 Java
         * 包名格式。
         *
         * @schemaExample com.sz.admin | 使用当前 admin 模块基础包名
         */
        private String packages = "com.sz.admin";

        /**
         * 导入数据库表时的表名前缀移除规则。当前导入流程直接读取本对象，实际使用生成器时应配置；缺省会导致导入流程无法读取 enabled 和 prefixes。
         */
        private IgnoreTablePrefix ignoreTablePrefix;
    }

    @Data
    public static class PathProperties {

        /**
         * 前端项目初始路径。非空时直接作为导入表和路径选项的前端目标；缺省或空白时，从后端项目根目录推导同级 sz-admin 目录。
         * 相对路径由后续文件操作按应用进程工作目录解析，建议配置明确路径。
         */
        private String web;

        /**
         * 后端代码初始目标路径。非空时直接作为导入表和路径选项的后端目标；缺省或空白时，本地环境优先选择扫描到的推荐模块， 其他情况回退到 module-name
         * 与 service-name 推导的模块目录。相对路径由后续文件操作按应用进程工作目录解析。
         */
        private String api;
    }

    @Data
    public static class IgnoreTablePrefix {

        /**
         * 导入数据库表时是否移除匹配的表名前缀。必须为非 null；启用后才遍历 prefixes，关闭时保留完整表名。
         */
        private Boolean enabled = false;

        /**
         * 按数组顺序匹配的表名前缀。enabled=true 且表名以某项开头时，移除首次命中的前缀后立即停止；null 表示没有可匹配项，
         * 空数组也不会修改表名。匹配区分大小写；命中项随后传给 replaceFirst，包含正则元字符可能改变替换结果或导致异常，建议
         * 使用普通字母、数字和下划线前缀。
         */
        private String[] prefixes = new String[]{"t_"};

    }

}
