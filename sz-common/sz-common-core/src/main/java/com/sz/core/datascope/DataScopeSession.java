package com.sz.core.datascope;

/**
 * 数据权限会话。
 * <p>
 * 配合 try-with-resources 使用，自动管理数据权限上下文的开启与清理， 无需手动调用
 * {@link SimpleDataScopeHelper#clearDataScope()}。
 *
 * <pre>{@code
 * try (var scope = new DataScopeSession(TeacherStatistics.class)) {
 *     return PageUtils.getPageResult(pageAs(...));
 * }
 * }</pre>
 *
 * @author sz
 */
public class DataScopeSession implements AutoCloseable {

    public DataScopeSession(Class<?> clazz) {
        SimpleDataScopeHelper.start(clazz);
    }

    @Override
    public void close() {
        SimpleDataScopeHelper.clearDataScope();
    }

}
