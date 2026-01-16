package com.sz.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.sz.admin.system.pojo.dto.common.ProxyDownloadDTO;
import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.ChallengeVO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import com.sz.admin.system.service.CommonService;
import com.sz.core.common.annotation.Debounce;
import com.sz.core.common.entity.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 通用controller
 *
 * @author sz
 * @version 1.0
 * @since 2023/12/25 10:07
 */
@Tag(name = "通用API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final CommonService commonService;

    @Operation(summary = "模板下载")
    @GetMapping("/download/templates")
    public void fileDownload(@RequestParam(value = "templateName", required = false) String templateName,
            @RequestParam(value = "alias", required = false) String alias, HttpServletResponse response) {
        try {
            commonService.tempDownload(templateName, alias, response);
        } catch (Exception e) {
            log.error("模板下载文件下载失败", e);
        }
    }

    @Operation(summary = "多维选择器查询")
    @GetMapping("/selector")
    public ApiResult<SelectorVO> querySelector(SelectorQueryDTO dto) {
        return ApiResult.success(commonService.querySelector(dto));
    }

    @SaIgnore
    @Debounce
    @GetMapping("/auth/challenge")
    @Operation(summary = "一次性认证参数，用于登录密码加密传输场景")
    public ApiResult<ChallengeVO> challenge() {
        return ApiResult.success(commonService.challenge());
    }

    @SaCheckLogin
    @GetMapping("oss/objects/private-url")
    @Operation(summary = "获取OSS私有文件访问URL")
    public ApiResult<String> getOssPrivateUrl(
            @Parameter(description = "OSS Bucket 名称，若不传则使用系统默认 Bucket", required = false, example = "my-bucket") @RequestParam(value = "bucket", required = false) String bucket,
            @Parameter(description = "存储在 OSS 中的原始对象地址（可为完整 URL 或 objectKey 对应的 URL）", required = true, example = "https://oss-example.com/my-bucket/path/to/file.png") @RequestParam("url") String url) {
        return ApiResult.success(commonService.ossPrivateUrl(bucket, url));
    }

    @SaCheckLogin
    @Operation(summary = "文件下载")
    @PostMapping("/files/download")
    public void proxyDownload(@RequestBody ProxyDownloadDTO dto, HttpServletResponse response) throws IOException {
        commonService.urlDownload(dto.getUrl(), response);
    }

}
