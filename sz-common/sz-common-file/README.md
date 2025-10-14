# sz-common-file 模块

该模块提供了基础的文件上传功能，包含以下特性：

## 功能特性

1. 基础文件上传
2. 指定路径文件上传
3. 可配置是否覆盖同名文件的上传

## 使用方法

在需要使用文件上传功能的模块中添加依赖：

```xml
<dependency>
    <groupId>com.sz</groupId>
    <artifactId>sz-common-file</artifactId>
    <version>${revision}</version>
</dependency>
```

然后在代码中注入 FileUploadService 使用：

```java
@Autowired
private FileUploadService fileUploadService;

// 基础上传
UploadResult result = fileUploadService.upload(file);

// 指定路径上传
UploadResult result = fileUploadService.upload(file, "custom/path/");

// 指定路径并设置是否覆盖上传
UploadResult result = fileUploadService.upload(file, "custom/path/", true);
```

## 配置项

在 application.yml 中可以配置以下参数：

```yaml
file:
  upload:
    # 默认基础路径
    base-path: uploads/
    # 最大文件大小 10MB
    max-file-size: 10485760
    # 是否允许覆盖同名文件
    allow-overwrite: false
```

## 本地测试

该模块设计为可以在没有OSS资源的情况下进行本地测试。模块默认使用本地文件系统进行文件存储。

### 本地测试方法

1. 在你的Spring Boot应用中添加依赖
2. 启动应用
3. 使用Postman或curl发送文件上传请求：
```bash
curl -X POST -F "file=@your-file.txt" http://localhost:8080/file/upload
```
4. 检查生成的文件是否在指定的本地路径中

### 注意事项

1. 确保应用程序有写入指定目录的权限
2. 上传的文件将存储在配置的 base-path 目录下
3. 文件名会被自动重命名为唯一标识符以避免冲突
4. 可以根据需要修改 application.yml 中的配置参数

详细使用示例请参考 [USAGE_EXAMPLE.md](USAGE_EXAMPLE.md) 文件。