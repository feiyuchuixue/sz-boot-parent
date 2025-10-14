# 本地测试文件上传功能指南

## 无需OSS的本地测试方法

本模块设计为可以在没有OSS资源的情况下进行本地测试。以下是如何在本地环境中测试文件上传功能的步骤：

## 1. 使用本地文件系统

模块默认使用本地文件系统进行文件存储，无需任何外部依赖。

### 配置参数
```yaml
file:
  upload:
    # 本地存储的基础路径
    base-path: uploads/
    # 最大文件大小 (默认10MB)
    max-file-size: 10485760
    # 是否允许覆盖同名文件
    allow-overwrite: false
```

### 测试方法

1. 在你的Spring Boot应用中添加依赖：
```xml
<dependency>
    <groupId>com.sz</groupId>
    <artifactId>sz-common-file</artifactId>
    <version>${revision}</version>
</dependency>
```

2. 在Controller中注入服务并使用：
```java
@RestController
@RequestMapping("/file")
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @PostMapping("/upload")
    public ResponseEntity<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        try {
            UploadResult result = fileUploadService.upload(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/uploadWithPath")
    public ResponseEntity<UploadResult> uploadWithPath(
            @RequestParam("file") MultipartFile file,
            @RequestParam("path") String path) {
        try {
            UploadResult result = fileUploadService.upload(file, path);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

## 2. 直接测试服务类

你可以通过单元测试直接验证功能：

```java
@SpringBootTest
public class FileUploadServiceTest {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Test
    public void testUpload() throws IOException {
        // 创建测试文件
        MockMultipartFile testFile = new MockMultipartFile(
            "test.txt", 
            "test.txt", 
            "text/plain", 
            "Hello World".getBytes()
        );
        
        // 测试基础上传
        UploadResult result = fileUploadService.upload(testFile);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getFilename());
        // ... 其他断言
    }
}
```

## 3. 运行和验证

1. 启动你的Spring Boot应用
2. 使用Postman或curl发送文件上传请求：
```bash
curl -X POST -F "file=@your-file.txt" http://localhost:8080/file/upload
```

3. 检查生成的文件是否在指定的本地路径中

## 注意事项

1. 确保应用程序有写入指定目录的权限
2. 上传的文件将存储在配置的 [base-path](file://D:\Business\school-project\admin-boot\config\local\oss.yml#L13-L13) 目录下
3. 文件名会被自动重命名为唯一标识符以避免冲突
4. 可以根据需要修改 [application.yml](file://D:\Business\school-project\admin-boot\sz-common\sz-common-file\src\test\resources\application.yml#L1-L5) 中的配置参数

通过这种方式，你可以在没有OSS资源的情况下完全测试文件上传功能。