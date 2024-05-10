package com.sz.admin.system.controller;

import com.sz.core.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用controller
 *
 * @ClassName CommonController
 * @Author sz
 * @Date 2023/12/25 10:07
 * @Version 1.0
 */
@Tag(name = "通用API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final ResourceLoader resourceLoader;

    @Operation(summary ="模板下载")
    @GetMapping("/download/templates")
    public void fileDownload(@RequestParam("templateName") String templateName, HttpServletResponse response) {
        try {
            FileUtils.downloadTemplateFile(resourceLoader, response, templateName);
        } catch (Exception e) {
            log.error("模板下载文件下载失败", e);
        }
    }


}
