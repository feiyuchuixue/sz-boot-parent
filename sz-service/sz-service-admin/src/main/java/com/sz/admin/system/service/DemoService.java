package com.sz.admin.system.service;

import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoListDTO;
import com.sz.admin.system.pojo.dto.demo.DemoUpdateDTO;
import com.sz.admin.system.pojo.vo.demo.DemoVO;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * Demo服务接口
 *
 * @author sz
 * @since 2025-01-14
 */
public interface DemoService {

    /**
     * 创建Demo
     */
    void create(DemoCreateDTO dto);

    /**
     * 更新Demo
     */
    void update(DemoUpdateDTO dto);

    /**
     * 删除Demo
     */
    void remove(SelectIdsDTO dto);

    /**
     * 分页查询Demo列表
     */
    PageResult<DemoVO> list(DemoListDTO dto);

    /**
     * 根据ID获取Demo详情
     */
    DemoVO getById(Long id);

    /**
     * 获取所有Demo列表（不分页）
     */
    List<DemoVO> getAll();
}