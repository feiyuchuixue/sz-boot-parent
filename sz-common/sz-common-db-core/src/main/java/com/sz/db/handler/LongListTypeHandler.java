package com.sz.db.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 兼容 MySQL（JSON 格式 [1,2,3]）和 PostgreSQL（原生数组格式 {1,2,3}）的 BIGINT 数组 TypeHandler。
 *
 * <ul>
 * <li>读取：自动识别 PG 的 {@code {1,2,3}} 和 MySQL 的 {@code [1,2,3]} 两种格式，统一转为
 * {@code List<Long>}</li>
 * <li>写入：优先使用 JDBC {@code createArrayOf}（PG）；若驱动不支持则退回写 JSON 字符串（MySQL）</li>
 * </ul>
 */
public class LongListTypeHandler extends BaseTypeHandler<List<Long>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        Connection conn = ps.getConnection();
        try {
            // PostgreSQL 支持 createArrayOf，优先使用原生数组
            Array array = conn.createArrayOf("bigint", parameter.toArray(new Long[0]));
            ps.setArray(i, array);
        } catch (SQLFeatureNotSupportedException | UnsupportedOperationException e) {
            // MySQL 不支持 createArrayOf，退回写 JSON 字符串
            ps.setString(i, toJsonArray(parameter));
        }
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs, columnName);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs, columnIndex);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String val = cs.getString(columnIndex);
        return val == null ? null : parseString(val);
    }

    // ---- 私有工具方法 ----

    /** 从 ResultSet 按列名读取，兼容 PG Array 对象和普通字符串 */
    private List<Long> parse(ResultSet rs, String columnName) throws SQLException {
        // 先尝试以 Array 读取（PG BIGINT[] 列）
        try {
            Array array = rs.getArray(columnName);
            if (array != null) {
                return fromArray(array);
            }
        } catch (Exception ignored) {
            // 不支持 getArray 时（MySQL），走字符串路径
        }
        String val = rs.getString(columnName);
        return val == null ? null : parseString(val);
    }

    /** 从 ResultSet 按列索引读取 */
    private List<Long> parse(ResultSet rs, int columnIndex) throws SQLException {
        try {
            Array array = rs.getArray(columnIndex);
            if (array != null) {
                return fromArray(array);
            }
        } catch (Exception ignored) {
        }
        String val = rs.getString(columnIndex);
        return val == null ? null : parseString(val);
    }

    /** 将 JDBC Array 转为 List<Long> */
    private List<Long> fromArray(Array array) throws SQLException {
        Object[] objs = (Object[]) array.getArray();
        List<Long> result = new ArrayList<>(objs.length);
        for (Object o : objs) {
            result.add(((Number) o).longValue());
        }
        return result;
    }

    /**
     * 解析字符串，兼容两种格式：
     * <ul>
     * <li>PG 格式：{@code {1,2,3}}</li>
     * <li>MySQL JSON 格式：{@code [1,2,3]}</li>
     * </ul>
     */
    private List<Long> parseString(String val) {
        String trimmed = val.trim();
        // 去掉外层括号（{ } 或 [ ]）
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        if (trimmed.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = trimmed.split(",");
        List<Long> result = new ArrayList<>(parts.length);
        for (String part : parts) {
            String p = part.trim();
            if (!p.isEmpty()) {
                result.add(Long.parseLong(p));
            }
        }
        return result;
    }

    /** 将 List<Long> 转为 JSON 数组字符串，用于 MySQL 写入 */
    private String toJsonArray(List<Long> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
