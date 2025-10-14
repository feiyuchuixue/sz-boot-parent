# 在实际项目中使用 sz-common-file 模块

## 添加依赖

在你的项目 pom.xml 中添加依赖：

```xml
<dependency>
    <groupId>com.sz</groupId>
    <artifactId>sz-common-file</artifactId>
    <version>${revision}</version>
</dependency>
```

## 使用示例

### 1. Controller 中使用

```java
package com.yourcompany.yourproject.controller;

import com.sz.file.FileUploadService;
import com.sz.file.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 基础文件上传
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        try {
            UploadResult result = fileUploadService.upload(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 指定路径上传
     */
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

    /**
     * 完整参数上传
     */
    @PostMapping("/uploadWithOverwrite")
    public ResponseEntity<UploadResult> uploadWithOverwrite(
            @RequestParam("file") MultipartFile file,
            @RequestParam("path") String path,
            @RequestParam("overwrite") boolean overwrite) {
        try {
            UploadResult result = fileUploadService.upload(file, path, overwrite);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

### 2. Service 中使用

```java
package com.yourcompany.yourproject.service;

import com.sz.file.FileUploadService;
import com.sz.file.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class YourBusinessService {

    @Autowired
    private FileUploadService fileUploadService;

    public void handleUserAvatarUpload(MultipartFile avatarFile, Long userId) throws IOException {
        // 上传用户头像到指定路径
        String path = "user/avatar/" + userId + "/";
        UploadResult result = fileUploadService.upload(avatarFile, path);
        
        // 处理上传结果，例如保存到数据库
        System.out.println("头像上传成功，访问URL: " + result.getUrl());
    }

    public void handleDocumentUpload(MultipartFile documentFile, String category) throws IOException {
        // 上传文档到指定路径，并允许覆盖同名文件
        String path = "documents/" + category + "/";
        UploadResult result = fileUploadService.upload(documentFile, path, true);
        
        // 处理上传结果
        System.out.println("文档上传成功，文件路径: " + result.getPath());
    }
}
```

### 3. 配置文件

在 application.yml 中配置文件上传参数：

```yaml
file:
  upload:
    # 基础路径
    base-path: uploads/
    # 最大文件大小 10MB
    max-file-size: 10485760
    # 是否允许覆盖同名文件
    allow-overwrite: false
```

## 测试上传功能

使用 curl 命令测试上传功能：

```bash
# 基础上传
curl -X POST -F "file=@test.txt" http://localhost:8080/api/file/upload

# 指定路径上传
curl -X POST -F "file=@test.txt" -F "path=custom/path/" http://localhost:8080/api/file/uploadWithPath

# 完整参数上传
curl -X POST -F "file=@test.txt" -F "path=custom/path/" -F "overwrite=true" http://localhost:8080/api/file/uploadWithOverwrite
```

## 注意事项

1. 确保应用有写入指定目录的权限
2. 上传的文件默认存储在配置的 base-path 目录下
3. 文件名会被自动重命名为唯一标识符防止冲突
4. 可根据需要调整 application.yml 中的配置参数