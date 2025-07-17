package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoListDTO;
import com.sz.admin.system.pojo.dto.demo.DemoUpdateDTO;
import com.sz.admin.system.pojo.vo.demo.DemoVO;
import com.sz.admin.system.service.DemoService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Demo服务实现类
 * 
 * 注意：这是一个演示实现，使用内存存储数据
 * 在实际项目中，应该使用数据库存储
 *
 * @author sz
 * @since 2025-01-14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DemoServiceImpl implements DemoService {

    // 使用内存存储演示数据（实际项目中应使用数据库）
    private final Map<Long, DemoVO> demoStore = new ConcurrentHashMap<>();
    private Long idGenerator = 1L;

    static {
        // 状态码对应的文本
    }

    @Override
    public void create(DemoCreateDTO dto) {
        log.info("创建Demo: {}", JsonUtils.toJsonString(dto));
        
        DemoVO demo = new DemoVO();
        demo.setId(idGenerator++);
        demo.setName(dto.getName());
        demo.setDescription(dto.getDescription());
        demo.setStatus(dto.getStatus());
        demo.setStatusText(getStatusText(dto.getStatus()));
        demo.setType(dto.getType());
        demo.setSort(dto.getSort() != null ? dto.getSort() : 0);
        demo.setCreateTime(LocalDateTime.now());
        demo.setUpdateTime(LocalDateTime.now());
        
        demoStore.put(demo.getId(), demo);
        log.info("Demo创建成功，ID: {}", demo.getId());
    }

    @Override
    public void update(DemoUpdateDTO dto) {
        log.info("更新Demo: {}", JsonUtils.toJsonString(dto));
        
        DemoVO existingDemo = demoStore.get(dto.getId());
        if (existingDemo == null) {
            CommonResponseEnum.NOT_EXISTS.message("Demo不存在").assertTrue(false);
        }
        
        existingDemo.setName(dto.getName());
        existingDemo.setDescription(dto.getDescription());
        existingDemo.setStatus(dto.getStatus());
        existingDemo.setStatusText(getStatusText(dto.getStatus()));
        existingDemo.setType(dto.getType());
        existingDemo.setSort(dto.getSort() != null ? dto.getSort() : 0);
        existingDemo.setUpdateTime(LocalDateTime.now());
        
        log.info("Demo更新成功，ID: {}", dto.getId());
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        log.info("删除Demo: {}", JsonUtils.toJsonString(dto));
        
        List<Long> ids = (List<Long>) dto.getIds();
        for (Long id : ids) {
            demoStore.remove(id);
        }
        
        log.info("Demo删除成功，数量: {}", ids.size());
    }

    @Override
    public PageResult<DemoVO> list(DemoListDTO dto) {
        log.info("分页查询Demo: {}", JsonUtils.toJsonString(dto));
        
        List<DemoVO> allDemos = new ArrayList<>(demoStore.values());
        
        // 过滤
        List<DemoVO> filteredDemos = allDemos.stream()
                .filter(demo -> {
                    if (dto.getName() != null && !dto.getName().isEmpty()) {
                        return demo.getName().contains(dto.getName());
                    }
                    return true;
                })
                .filter(demo -> {
                    if (dto.getStatus() != null) {
                        return demo.getStatus().equals(dto.getStatus());
                    }
                    return true;
                })
                .filter(demo -> {
                    if (dto.getType() != null && !dto.getType().isEmpty()) {
                        return dto.getType().equals(demo.getType());
                    }
                    return true;
                })
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
        
        // 分页
        int pageNum = dto.getPage() != null ? dto.getPage() : 1;
        int pageSize = dto.getLimit() != null ? dto.getLimit() : 10;
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredDemos.size());
        
        List<DemoVO> pageData = filteredDemos.subList(Math.max(0, start), Math.max(0, end));
        
        long totalPage = (filteredDemos.size() + pageSize - 1) / pageSize;
        PageResult<DemoVO> result = new PageResult<>(pageNum, pageSize, totalPage, filteredDemos.size(), pageData);
        
        return result;
    }

    @Override
    public DemoVO getById(Long id) {
        log.info("查询Demo详情，ID: {}", id);
        
        DemoVO demo = demoStore.get(id);
        if (demo == null) {
            CommonResponseEnum.NOT_EXISTS.message("Demo不存在").assertTrue(false);
        }
        
        return demo;
    }

    @Override
    public List<DemoVO> getAll() {
        log.info("查询所有Demo");
        
        return new ArrayList<>(demoStore.values())
                .stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
    
    private String getStatusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "禁用";
            case 1 -> "启用";
            default -> "未知";
        };
    }
}