package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.vo.systempfile.SysTempFileInfoVO;
import com.sz.admin.system.service.CommonService;
import com.sz.admin.system.service.SysTempFileService;
import com.sz.core.util.FileUtils;
import com.sz.oss.OssClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static com.sz.core.common.enums.CommonResponseEnum.FILE_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final ResourceLoader resourceLoader;

    private final SysTempFileService sysTempFileService;

    private final OssClient ossClient;

    @Override
    public void tempDownload(String templateName, HttpServletResponse response) throws IOException {
        String templatePath = "classpath:/templates/" + templateName;
        Resource resource = resourceLoader.getResource(templatePath);

        // 兼容临时目录文件。 TODO： ！！建议迁移至OSS
        if (resource.exists()) {
            FileUtils.downloadTemplateFile(resourceLoader, response, templateName);
            return;
        }

        // 从oss获取文件
        SysTempFileInfoVO sysTempFileInfoVO = sysTempFileService.detailByName(templateName);
        // 异常情况处理。通过http status响应
        if (sysTempFileInfoVO == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setStatus(FILE_NOT_EXISTS.getCode());
            PrintWriter writer = response.getWriter();
            writer.println(FILE_NOT_EXISTS.getMessage());
            writer.flush();
            return;
        }
        FILE_NOT_EXISTS.assertNull(sysTempFileInfoVO);
        String objectName = sysTempFileInfoVO.getObjectName();
        String fileName = sysTempFileInfoVO.getTempName();
        ossClient.download(objectName, response, fileName);

    }

}
