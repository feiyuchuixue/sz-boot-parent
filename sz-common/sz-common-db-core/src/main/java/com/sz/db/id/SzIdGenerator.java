package com.sz.db.id;

import cn.hutool.core.lang.Snowflake;
import com.mybatisflex.core.keygen.IKeyGenerator;

/**
 * sz 框架统一雪花 ID 生成器。
 * <p>
 * 基于 Hutool {@link Snowflake} 实现，workerId / datacenterId 通过
 * {@link SzSnowflakeProperties} 显式配置，避免自动推导在容器环境下的碰撞风险。
 * <p>
 * 注册名称 {@link #NAME}，实体使用方式：
 * 
 * <pre>
 *   {@code @Id(keyType = KeyType.Generator, value = SzIdGenerator.NAME)}
 *   private Long id;
 * </pre>
 *
 * @author sz
 */
public class SzIdGenerator implements IKeyGenerator {

    /**
     * 注册到 MyBatis-Flex 的 KeyGenerator 名称。
     */
    public static final String NAME = "szSnowflakeId";

    private final Snowflake snowflake;

    /**
     * 接收外部统一管理的 {@link Snowflake} 单例，确保与业务层共用同一实例，避免多实例导致 ID 碰撞。
     */
    public SzIdGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Object generate(Object entity, String idFieldName) {
        return snowflake.nextId();
    }

}
