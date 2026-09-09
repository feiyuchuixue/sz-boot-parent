package com.sz.db.id;

import cn.hutool.core.lang.Snowflake;
import org.springframework.stereotype.Component;

/**
 * sz 框架统一雪花 ID 工具类。
 * <p>
 * 对外屏蔽 Hutool {@link Snowflake} 细节，业务层通过 {@link #nextId()} 静态方法 获取全局唯一 ID，底层复用
 * Spring 容器中由 {@code sz.id.worker-id} / {@code sz.id.datacenter-id} 配置驱动的单例
 * {@link Snowflake} Bean， 与 MyBatis-Flex {@link SzIdGenerator} 共用同一实例，保证 ID
 * 空间不冲突。
 *
 * <pre>
 * 使用示例：
 *   long id = SzIdUtil.nextId();
 * </pre>
 *
 * @author sz
 */
@Component
public class SzIdUtil {

    private static Snowflake snowflake;

    /**
     * 由 Spring 容器注入全局唯一 {@link Snowflake} 单例。 构造注入保证在任何 {@link #nextId()}
     * 调用前已完成初始化。
     */
    public SzIdUtil(Snowflake snowflake) {
        SzIdUtil.snowflake = snowflake;
    }

    /**
     * 生成全局唯一雪花 ID。
     *
     * @return 雪花 ID（long 类型）
     */
    public static long nextId() {
        return snowflake.nextId();
    }

}
