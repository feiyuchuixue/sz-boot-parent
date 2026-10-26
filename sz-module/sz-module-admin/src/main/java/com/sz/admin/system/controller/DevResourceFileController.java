package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.service.SysResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 本地开发临时文件服务，仅在 dev-file-server.enabled 显式为 true 时注册，不额外限制 profile。
 * <p>
 * 匿名读取唯一匹配的 LOCAL DIRECT 场景。部署环境使用 Nginx；关闭本 Controller 不影响上传等资源接口。
 */
@Tag(name = "本地开发文件服务")
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "sz.resource.dev-file-server", name = "enabled", havingValue = "true", matchIfMissing = false)
public class DevResourceFileController {

    private final SysResourceService sysResourceService;

    @SaIgnore
    @Operation(summary = "本地开发文件访问", description = "仅供本地临时验证，匿名读取 LOCAL DIRECT 文件；部署环境请使用 Nginx 静态服务。")
    @GetMapping("/file/{sceneDir}/**")
    public ResponseEntity<StreamingResponseBody> serveFile(@Parameter(description = "场景 base-url 的最后一个路径段") @PathVariable String sceneDir,
            HttpServletRequest request) {
        return sysResourceService.findServeFile(sceneDir, request);
    }
}
