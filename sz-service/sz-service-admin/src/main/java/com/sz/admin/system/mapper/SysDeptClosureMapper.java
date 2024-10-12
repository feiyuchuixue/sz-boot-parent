package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysDeptClosure;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 部门祖籍关系表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2024-03-28
 */
public interface SysDeptClosureMapper extends BaseMapper<SysDeptClosure> {

    /**
     * 分离子树
     *
     * @param nodeId
     * @return
     */
    @Delete(" DELETE " + " FROM " + " sys_dept_closure t " + " WHERE" + " EXISTS (" + " SELECT" + " 1 " + " FROM" + " sys_dept_closure d " + " WHERE"
            + " d.ancestor_id = #{nodeId} and t.descendant_id = d.descendant_id " + " ) " + " and "
            + " EXISTS( select 1 from sys_dept_closure a where a.descendant_id = #{nodeId} and a.ancestor_id != a.descendant_id and t.ancestor_id = a.ancestor_id )")
    Integer detach(@Param("nodeId") Integer nodeId);

    /**
     * 查询指定节点的子树
     * 
     * @param nodeId
     * @return
     */
    @Select(" SELECT ancestor_id,descendant_id,depth FROM sys_dept_closure " + " WHERE EXISTS (" + "    SELECT 1 " + "    FROM sys_dept_closure d "
            + "    WHERE d.ancestor_id = #{nodeId} " + "    AND sys_dept_closure.descendant_id = d.descendant_id" + ")" + " AND EXISTS (" + "    SELECT 1 "
            + "    FROM sys_dept_closure a " + "    WHERE a.descendant_id = #{nodeId} " + "    AND a.ancestor_id != a.descendant_id "
            + "    AND sys_dept_closure.ancestor_id = a.ancestor_id " + " );")
    List<SysDeptClosure> selectDetachTree(@Param("nodeId") Long nodeId);

    /**
     * 嫁接子树
     *
     * @param nodeId
     * @param newNodeId
     * @return
     */
    @Insert("INSERT INTO sys_dept_closure ( ancestor_id, descendant_id, depth ) SELECT" + " super.ancestor_id," + " sub.descendant_id,("
            + " super.depth + sub.depth + 1 " + " ) " + " FROM" + " sys_dept_closure super" + " CROSS JOIN sys_dept_closure sub " + " WHERE"
            + " super.descendant_id = #{newNodeId} " + " AND sub.ancestor_id = #{nodeId}")
    Integer graft(@Param("nodeId") Long nodeId, @Param("newNodeId") Long newNodeId);

}