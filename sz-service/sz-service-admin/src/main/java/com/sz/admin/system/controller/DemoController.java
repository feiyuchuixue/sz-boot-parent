package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoListDTO;
import com.sz.admin.system.pojo.dto.demo.DemoUpdateDTO;
import com.sz.admin.system.pojo.vo.demo.DemoVO;
import com.sz.admin.system.service.DemoService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.valid.annotation.NotZero;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Demo控制器 - 演示各种请求类型的使用
 * 
 * 这个控制器展示了常见的CRUD操作和不同的HTTP请求方法：
 * - GET: 查询操作
 * - POST: 创建操作  
 * - PUT: 更新操作
 * - DELETE: 删除操作
 *
 * @author sz
 * @since 2025-01-14
 */
@Tag(name = "Demo管理", description = "演示请求的各种用法")
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
@Slf4j
public class DemoController {

    private final DemoService demoService;

    @Operation(summary = "创建Demo", description = "使用POST请求创建新的Demo数据")
    @PostMapping
    public ApiResult<Void> create(@Valid @RequestBody DemoCreateDTO dto) {
        log.info("创建Demo请求: {}", dto);
        demoService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "更新Demo", description = "使用PUT请求更新现有的Demo数据")
    @PutMapping
    public ApiResult<Void> update(@Valid @RequestBody DemoUpdateDTO dto) {
        log.info("更新Demo请求: {}", dto);
        demoService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除Demo", description = "使用DELETE请求删除Demo数据，支持批量删除")
    @DeleteMapping
    public ApiResult<Void> remove(@Valid @RequestBody SelectIdsDTO dto) {
        log.info("删除Demo请求: {}", dto);
        demoService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "分页查询Demo列表", description = "使用GET请求进行分页查询，支持多种筛选条件")
    @GetMapping("/list")
    public ApiResult<PageResult<DemoVO>> list(DemoListDTO dto) {
        log.info("分页查询Demo请求: {}", dto);
        PageResult<DemoVO> result = demoService.list(dto);
        return ApiResult.success(result);
    }

    @Operation(summary = "根据ID查询Demo详情", description = "使用GET请求根据路径参数获取单个Demo详情")
    @GetMapping("/{id}")
    public ApiResult<DemoVO> getById(@PathVariable @NotZero Long id) {
        log.info("查询Demo详情请求，ID: {}", id);
        DemoVO result = demoService.getById(id);
        return ApiResult.success(result);
    }

    @Operation(summary = "查询所有Demo", description = "使用GET请求获取所有Demo数据（不分页）")
    @GetMapping("/all")
    public ApiResult<List<DemoVO>> getAll() {
        log.info("查询所有Demo请求");
        List<DemoVO> result = demoService.getAll();
        return ApiResult.success(result);
    }

    @Operation(summary = "根据请求参数查询Demo", description = "演示使用@RequestParam接收单个参数")
    @GetMapping("/search")
    public ApiResult<List<DemoVO>> search(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) Integer status) {
        log.info("搜索Demo请求，name: {}, status: {}", name, status);
        
        DemoListDTO dto = new DemoListDTO();
        dto.setName(name);
        dto.setStatus(status);
        
        PageResult<DemoVO> result = demoService.list(dto);
        return ApiResult.success(result.getRows());
    }

    @Operation(summary = "检查Demo名称是否存在", description = "演示简单的业务逻辑检查")
    @GetMapping("/check-name")
    public ApiResult<Boolean> checkName(@RequestParam String name) {
        log.info("检查Demo名称请求: {}", name);
        
        List<DemoVO> allDemos = demoService.getAll();
        boolean exists = allDemos.stream()
                .anyMatch(demo -> demo.getName().equals(name));
        
        return ApiResult.success(exists);
    }

    @Operation(summary = "批量更新Demo状态", description = "演示批量操作")
    @PutMapping("/batch-status")
    public ApiResult<Void> batchUpdateStatus(@RequestBody SelectIdsDTO dto, 
                                             @RequestParam Integer status) {
        log.info("批量更新Demo状态请求，IDs: {}, status: {}", dto.getIds(), status);
        
        List<Long> ids = (List<Long>) dto.getIds();
        for (Long id : ids) {
            DemoVO demo = demoService.getById(id);
            if (demo != null) {
                DemoUpdateDTO updateDto = new DemoUpdateDTO();
                updateDto.setId(id);
                updateDto.setName(demo.getName());
                updateDto.setDescription(demo.getDescription());
                updateDto.setStatus(status);
                updateDto.setType(demo.getType());
                updateDto.setSort(demo.getSort());
                demoService.update(updateDto);
            }
        }
        
        return ApiResult.success();
    }
}