package com.sz.demo;

import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoListDTO;
import com.sz.admin.system.pojo.dto.demo.DemoUpdateDTO;
import com.sz.admin.system.pojo.vo.demo.DemoVO;
import com.sz.admin.system.service.impl.DemoServiceImpl;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * Demo API手动验证
 * 
 * @author sz
 * @since 2025-01-14
 */
public class DemoApiVerification {
    
    public static void main(String[] args) {
        System.out.println("=== Demo API 功能验证 ===");
        
        DemoServiceImpl demoService = new DemoServiceImpl();
        
        try {
            // 1. 创建Demo
            System.out.println("\n1. 创建Demo测试:");
            DemoCreateDTO createDto = new DemoCreateDTO();
            createDto.setName("测试Demo");
            createDto.setDescription("这是一个用于演示的Demo");
            createDto.setStatus(1);
            createDto.setType("example");
            createDto.setSort(1);
            
            demoService.create(createDto);
            System.out.println("✅ Demo创建成功");
            
            // 2. 创建第二个Demo
            DemoCreateDTO createDto2 = new DemoCreateDTO();
            createDto2.setName("第二个Demo");
            createDto2.setDescription("另一个测试Demo");
            createDto2.setStatus(0);
            createDto2.setType("test");
            createDto2.setSort(2);
            
            demoService.create(createDto2);
            System.out.println("✅ 第二个Demo创建成功");
            
            // 3. 查询所有Demo
            System.out.println("\n2. 查询所有Demo:");
            List<DemoVO> allDemos = demoService.getAll();
            System.out.println("总共创建了 " + allDemos.size() + " 个Demo:");
            for (DemoVO demo : allDemos) {
                System.out.println("- ID: " + demo.getId() + ", 名称: " + demo.getName() + 
                                 ", 状态: " + demo.getStatusText() + ", 类型: " + demo.getType());
            }
            
            // 4. 根据ID查询
            System.out.println("\n3. 根据ID查询Demo:");
            Long firstId = allDemos.get(0).getId();
            DemoVO demoById = demoService.getById(firstId);
            System.out.println("查询到的Demo: " + demoById.getName() + 
                             " (创建时间: " + demoById.getCreateTime() + ")");
            
            // 5. 分页查询
            System.out.println("\n4. 分页查询测试:");
            DemoListDTO listDto = new DemoListDTO();
            listDto.setPage(1);
            listDto.setLimit(1);
            
            var pageResult = demoService.list(listDto);
            System.out.println("分页查询结果: 总数=" + pageResult.getTotal() + 
                             ", 当前页数据量=" + pageResult.getRows().size());
            
            // 6. 带过滤条件的查询
            System.out.println("\n5. 过滤查询测试:");
            listDto.setName("测试");
            listDto.setLimit(10);
            var filteredResult = demoService.list(listDto);
            System.out.println("名称包含'测试'的Demo数量: " + filteredResult.getTotal());
            
            // 7. 更新Demo
            System.out.println("\n6. 更新Demo测试:");
            DemoUpdateDTO updateDto = new DemoUpdateDTO();
            updateDto.setId(firstId);
            updateDto.setName("更新后的Demo");
            updateDto.setDescription("这是更新后的描述");
            updateDto.setStatus(0);
            updateDto.setType("updated");
            updateDto.setSort(99);
            
            demoService.update(updateDto);
            
            DemoVO updatedDemo = demoService.getById(firstId);
            System.out.println("✅ Demo更新成功: " + updatedDemo.getName() + 
                             " (状态: " + updatedDemo.getStatusText() + ")");
            
            // 8. 删除Demo
            System.out.println("\n7. 删除Demo测试:");
            SelectIdsDTO deleteDto = new SelectIdsDTO();
            deleteDto.setIds(List.of(firstId));
            
            demoService.remove(deleteDto);
            
            List<DemoVO> remainingDemos = demoService.getAll();
            System.out.println("✅ Demo删除成功，剩余Demo数量: " + remainingDemos.size());
            
            System.out.println("\n=== 所有Demo API功能验证通过！ ===");
            
        } catch (Exception e) {
            System.err.println("❌ 验证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}