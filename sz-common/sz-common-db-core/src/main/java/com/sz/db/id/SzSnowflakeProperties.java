package com.sz.db.id;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花 ID 生成器配置属性。
 * <p>
 * 生产环境必须通过环境变量 SZ_WORKER_ID / SZ_DATACENTER_ID 显式指定， 防止多节点部署时 workerId 碰撞。
 *
 * @author sz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sz.id")
public class SzSnowflakeProperties {

    /**
     * 工作机器 ID（0-31），生产环境必须显式配置。 默认值 1
     */
    private long workerId = 1L;

    /**
     * 数据中心 ID（0-31），生产环境必须显式配置。 默认值 1
     */
    private long datacenterId = 1L;

}
