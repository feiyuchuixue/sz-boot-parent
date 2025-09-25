package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import com.sz.admin.system.service.CommonService;
import com.sz.core.common.entity.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 通用controller
 *
 * @author sz
 * @since 2023/12/25 10:07
 * @version 1.0
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

}
