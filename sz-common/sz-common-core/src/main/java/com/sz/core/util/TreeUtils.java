package com.sz.core.util;

import com.sz.core.common.service.Treeable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 树形递归，用于构建以id、pid为关联标识的树形结构
 *
 * @ClassName TreeUtil
 * @Author sz
 * @Date 2024/3/22 17:48
 * @Version 1.0
 */
public class TreeUtils {

    /**
     * 构建树形结构
     *
     * @param allDepts
     *            所有部门列表
     * @param root
     *            自定义根节点属性
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
     * 构建树形结构（支持排除指定节点）
     *
     * @param allDepts
     *            所有部门列表
     * @param excludeNodeId
     *            排除节点的 ID
     * @param root
     *            自定义根节点属性
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
     * 递归构建树形结构
     *
     * @param parent
     *            父节点
     * @param allDepts
     *            所有部门列表
     * @param <T>
     *            树节点类型
     */
    private static <T extends Treeable<T>> void constructTreeRecursive(T parent, List<T> allDepts) {
        // 遍历所有部门
        for (T dept : allDepts) {
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            // 如果部门的父级ID等于当前父部门的ID
            if (dept.getPid().equals(parent.getId())) {
                // 递归构建子部门的子部门
                constructTreeRecursive(dept, allDepts);
                // 将子部门添加到父部门的子级列表中
                parent.getChildren().add(dept);
            }
        }
    }

    /**
     * 递归构建树形结构（支持排除指定节点）
     *
     * @param parent
     *            父节点
     * @param allDepts
     *            所有部门列表
     * @param excludeNodeId
     *            排除节点的 ID
     * @param <T>
     *            树节点类型
     */
    private static <T extends Treeable<T>> void constructTreeRecursiveExcludeNode(T parent, List<T> allDepts, Object excludeNodeId) {
        // 遍历所有节点
        for (T dept : allDepts) {
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            // 如果部门的父级ID等于当前父部门的ID，并且部门的ID不等于排除节点的ID
            if (dept.getPid().equals(parent.getId()) && !dept.getId().toString().equals(excludeNodeId.toString())) {
                // 递归构建子部门的子部门
                constructTreeRecursiveExcludeNode(dept, allDepts, excludeNodeId);

                // 将子部门添加到父部门的子级列表中
                parent.getChildren().add(dept);
            }
        }
    }

    /**
     * 获取根节点
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
            throw new RuntimeException(e);
        }
    }

}
