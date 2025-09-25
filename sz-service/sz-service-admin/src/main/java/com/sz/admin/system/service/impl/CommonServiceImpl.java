package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileInfoVO;
import com.sz.admin.system.service.*;
import com.sz.core.util.FileUtils;
import com.sz.oss.OssClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.sz.core.common.enums.CommonResponseEnum.FILE_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final ResourceLoader resourceLoader;

    private final SysTempFileService sysTempFileService;

    private final OssClient ossClient;

    private final SysUserService sysUserService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    @Override
    public void tempDownload(String templateName, String alias, HttpServletResponse response) throws IOException {
        String templatePath = "classpath:/templates/" + templateName;
        Resource resource = resourceLoader.getResource(templatePath);

        // 兼容临时目录文件。
        if (resource.exists()) {
            FileUtils.downloadTemplateFile(resourceLoader, response, templateName);
            return;
        }

        // 从oss获取文件
        SysTempFileInfoVO sysTempFileInfoVO = sysTempFileService.detailByNameOrAlias(templateName, alias);
        // 异常情况处理。通过http status响应
        if (sysTempFileInfoVO == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setStatus(FILE_NOT_EXISTS.getCode());
            OutputStream out = response.getOutputStream();
            out.write(FILE_NOT_EXISTS.getMessage().getBytes(StandardCharsets.UTF_8));
            out.flush();
            return;
        }
        FILE_NOT_EXISTS.assertNull(sysTempFileInfoVO);
        String objectName = sysTempFileInfoVO.getObjectName();
        String fileName = sysTempFileInfoVO.getTempName();
        ossClient.download(objectName, response, fileName);

    }

    @Override
    public SelectorVO querySelector(SelectorQueryDTO queryDTO) {
        SelectorVO result = new SelectorVO();
        String type = queryDTO.getType();
        Object data;
        switch (type) {
            case "user" -> data = sysUserService.pageSelector(queryDTO);
            case "role" -> data = sysRoleService.pageSelector(queryDTO);
            case "department" -> data = sysDeptService.listSelector(queryDTO);
            default -> throw new RuntimeException("不支持的维度类型: " + type);
        }
        result.setType(type);
        result.setData(data);
        return result;
    }

}
