package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoListDTO;
import com.sz.admin.system.pojo.vo.demo.DemoVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo服务集成测试
 * 
 * @author sz
 * @since 2025-01-14
 */
class DemoServiceImplManualTest {

    @Test
    void manualTestDemoService() {
        DemoServiceImpl demoService = new DemoServiceImpl();
        
        // 1. 测试创建Demo
        DemoCreateDTO createDto = new DemoCreateDTO();
        createDto.setName("测试Demo");
        createDto.setDescription("这是一个测试用的Demo");
        createDto.setStatus(1);
        createDto.setType("test");
        createDto.setSort(1);
        
        demoService.create(createDto);
        
        // 2. 测试查询所有Demo
        var allDemos = demoService.getAll();
        assertEquals(1, allDemos.size());
        
        DemoVO firstDemo = allDemos.get(0);
        assertEquals("测试Demo", firstDemo.getName());
        assertEquals("这是一个测试用的Demo", firstDemo.getDescription());
        assertEquals(1, firstDemo.getStatus());
        assertEquals("启用", firstDemo.getStatusText());
        assertEquals("test", firstDemo.getType());
        assertEquals(1, firstDemo.getSort());
        assertNotNull(firstDemo.getCreateTime());
        assertNotNull(firstDemo.getUpdateTime());
        
        // 3. 测试根据ID查询
        DemoVO demoById = demoService.getById(firstDemo.getId());
        assertEquals(firstDemo.getName(), demoById.getName());
        
        // 4. 测试分页查询
        DemoListDTO listDto = new DemoListDTO();
        listDto.setPage(1);
        listDto.setLimit(10);
        
        var pageResult = demoService.list(listDto);
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getRows().size());
        
        // 5. 测试带过滤条件的查询
        listDto.setName("测试");
        var filteredResult = demoService.list(listDto);
        assertEquals(1, filteredResult.getTotal());
        
        listDto.setName("不存在");
        var emptyResult = demoService.list(listDto);
        assertEquals(0, emptyResult.getTotal());
        
        System.out.println("✅ Demo API 手动测试通过！");
        System.out.println("创建的Demo: " + firstDemo.getName());
        System.out.println("Demo状态: " + firstDemo.getStatusText());
        System.out.println("创建时间: " + firstDemo.getCreateTime());
    }
}