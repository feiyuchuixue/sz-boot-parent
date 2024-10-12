package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptCreateDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptListDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptUpdateDTO;
import com.sz.admin.system.pojo.po.SysDept;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptLeaderVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 部门表 Service
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
public interface SysDeptService extends IService<SysDept> {

    void create(SysDeptCreateDTO dto);

    void update(SysDeptUpdateDTO dto);

    PageResult<SysDeptVO> page(SysDeptListDTO dto);

    List<SysDeptVO> list(SysDeptListDTO dto);

    void remove(SelectIdsDTO dto);

    SysDeptVO detail(Object id);

    List<DeptTreeVO> getDepartmentTreeWithAdditionalNodes();

    List<DeptTreeVO> getDeptTree(Integer excludeNodeId, Boolean appendRoot, Boolean needSetTotal);

    SysDeptLeaderVO findSysUserDeptLeader();
}