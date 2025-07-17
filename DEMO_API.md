# Demo API 请求示例

这个Demo API展示了在sz-boot-parent项目中如何实现各种类型的HTTP请求。

## API概览

Demo API提供了完整的CRUD操作示例，包括：

### 1. 创建Demo (POST请求)
```http
POST /demo
Content-Type: application/json

{
    "name": "示例Demo",
    "description": "这是一个演示Demo",
    "status": 1,
    "type": "example",
    "sort": 1
}
```

### 2. 更新Demo (PUT请求)
```http
PUT /demo
Content-Type: application/json

{
    "id": 1,
    "name": "更新的Demo",
    "description": "这是一个更新后的Demo",
    "status": 1,
    "type": "example",
    "sort": 2
}
```

### 3. 删除Demo (DELETE请求)
```http
DELETE /demo
Content-Type: application/json

{
    "ids": [1, 2, 3]
}
```

### 4. 分页查询Demo列表 (GET请求)
```http
GET /demo/list?page=1&limit=10&name=示例&status=1&type=example
```

### 5. 根据ID查询Demo详情 (GET请求)
```http
GET /demo/1
```

### 6. 查询所有Demo (GET请求)
```http
GET /demo/all
```

### 7. 搜索Demo (GET请求)
```http
GET /demo/search?name=示例&status=1
```

### 8. 检查名称是否存在 (GET请求)
```http
GET /demo/check-name?name=示例Demo
```

### 9. 批量更新状态 (PUT请求)
```http
PUT /demo/batch-status?status=0
Content-Type: application/json

{
    "ids": [1, 2, 3]
}
```

## 技术特性

1. **参数验证**: 使用`@Valid`和`@NotBlank`等注解进行参数验证
2. **API文档**: 使用Swagger/OpenAPI注解生成API文档
3. **统一响应**: 使用`ApiResult`包装所有API响应
4. **异常处理**: 使用枚举类统一管理异常信息
5. **日志记录**: 记录所有API调用和参数信息

## 数据结构

### DemoCreateDTO (创建请求)
- name: 名称（必填）
- description: 描述（可选）
- status: 状态（必填，0=禁用，1=启用）
- type: 类型（可选）
- sort: 排序（可选）

### DemoUpdateDTO (更新请求)
- id: ID（必填）
- name: 名称（必填）
- description: 描述（可选）
- status: 状态（必填）
- type: 类型（可选）
- sort: 排序（可选）

### DemoVO (响应对象)
- id: ID
- name: 名称
- description: 描述
- status: 状态
- statusText: 状态描述
- type: 类型
- sort: 排序
- createTime: 创建时间
- updateTime: 更新时间

## 注意事项

1. 此Demo使用内存存储数据，重启后数据会丢失
2. 在实际项目中应该使用数据库存储数据
3. 所有API都遵循RESTful设计原则
4. 支持Knife4j文档，可在浏览器中查看详细API文档

## 使用场景

这个Demo API可以作为：
- 学习Spring Boot开发的参考
- 新API开发的模板
- 前端开发的Mock数据源
- API设计规范的示例