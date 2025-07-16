package com.sz.core.util;

import com.sz.core.common.service.Treeable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类，用于构建以 ID 和 PID 为关联标识的树形结构。
 *
 * @since 2024-03-22
 * @version 1.0
 */
public class TreeUtils {

    private TreeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 构建树形结构。
     *
     * @param allDepts
     *            所有部门列表
     * @param root
     *            自定义根节点
     * @param <T>
     *            树节点类型
     * @return 树形结构列表
     */
    public static <T extends Treeable<T>> List<T> buildTree(List<T> allDepts, T root) {
        List<T> trees = new ArrayList<>();
        // 构建树形结构
        constructTreeRecursive(root, allDepts);
        trees.add(root);
        return trees;
    }

    /**
     * 构建树形结构，支持排除指定节点。
     *
     * @param allDepts
     *            所有部门列表
     * @param excludeNodeId
     *            排除的节点 ID
     * @param root
     *            自定义根节点
     * @param <T>
     *            树节点类型
     * @return 树形结构列表
     */
    public static <T extends Treeable<T>> List<T> buildTree(List<T> allDepts, T root, Object excludeNodeId) {
        List<T> trees = new ArrayList<>();
        // 构建树形结构
        if (excludeNodeId != null) {
            constructTreeRecursiveExcludeNode(root, allDepts, excludeNodeId);
        } else {
            constructTreeRecursive(root, allDepts);
        }
        trees.add(root);
        return trees;
    }

    /**
     * 构建树形结构（不需要自定义根节点）。 自动查找所有顶级节点（pid == null 或 pid == -1），并递归构建树。
     *
     * @param allDepts
     *            所有部门列表
     * @param <T>
     *            树节点类型
     * @return 树形结构列表
     */
    public static <T extends Treeable<T>> List<T> buildTree(List<T> allDepts, Object startNodeId) {
        List<T> roots = new ArrayList<>();
        if (allDepts == null || allDepts.isEmpty()) {
            return roots;
        }
        for (T node : allDepts) {
            Object pid = node.getPid();
            if (pid == null || String.valueOf(startNodeId).equals(String.valueOf(pid))) {
                constructTreeRecursive(node, allDepts);
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * 递归构建树形结构。
     *
     * @param parent
     *            父节点
     * @param allDepts
     *            所有部门列表
     * @param <T>
     *            树节点类型
     */
    private static <T extends Treeable<T>> void constructTreeRecursive(T parent, List<T> allDepts) {
        for (T dept : allDepts) {
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            // 类型兼容比较
            if (parent.getId() != null && dept.getPid() != null && String.valueOf(dept.getPid()).equals(String.valueOf(parent.getId()))) {
                // 递归构建子部门的子部门
                constructTreeRecursive(dept, allDepts);
                // 将子部门添加��父部门的子级列表中
                parent.getChildren().add(dept);
            }
        }
    }

    /**
     * 递归构建树形结构，支持排除指定节点。
     *
     * @param parent
     *            父节点
     * @param allDepts
     *            所有部门列表
     * @param excludeNodeId
     *            排除的节点 ID
     * @param <T>
     *            树节点类型
     */
    private static <T extends Treeable<T>> void constructTreeRecursiveExcludeNode(T parent, List<T> allDepts, Object excludeNodeId) {
        for (T dept : allDepts) {
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            // 类型兼容比较
            if (parent.getId() != null && dept.getPid() != null && String.valueOf(dept.getPid()).equals(String.valueOf(parent.getId()))
                    && !String.valueOf(dept.getId()).equals(String.valueOf(excludeNodeId))) {
                // 递归构建子部门的子部门
                constructTreeRecursiveExcludeNode(dept, allDepts, excludeNodeId);

                // 将子部门添加到父部门的子级列表中
                parent.getChildren().add(dept);
            }
        }
    }

    /**
     * 获取树的根节点。
     *
     * @param clazz
     *            树节点类型
     * @param <T>
     *            树节点类型
     * @return 根节点
     */
    public static <T extends Treeable<T>> T getRoot(Class<T> clazz) {
        try {
            T root = clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("setId", Long.class).invoke(root, 0L);
            clazz.getMethod("setPid", Long.class).invoke(root, -1L);
            return root;
        } catch (InstantiationException | IllegalAccessException e) {
            // 处理异常
            return null;
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
