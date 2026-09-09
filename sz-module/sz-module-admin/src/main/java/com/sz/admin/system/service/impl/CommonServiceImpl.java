package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.ChallengeVO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileInfoVO;
import com.sz.admin.system.service.*;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.*;
import com.sz.platform.constant.config.LoginConfigKeyConstant;
import com.sz.excel.core.ExcelTemplateScanRegistry;
import com.sz.excel.utils.ExcelUtils;
import com.sz.oss.OssClient;
import com.sz.redis.RedisCache;
import com.sz.resource.model.ResourceRef;
import com.sz.resource.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.sz.core.common.enums.CommonResponseEnum.FILE_NOT_EXISTS;
import static com.sz.core.common.enums.CommonResponseEnum.FILE_TEMPLATE_INVALID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final ResourceLoader resourceLoader;

    private final SysTempFileService sysTempFileService;

    private final OssClient ossClient;

    private final SysUserService sysUserService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    private final RedisCache redisCache;

    private final ObjectProvider<ExcelTemplateScanRegistry> excelTemplateScanRegistryProvider;

    private final ResourceService resourceService;

    @Override
    public void tempDownload(String templateName, String alias, HttpServletResponse response) throws IOException {
        boolean hasIllegalPath = templateName.contains("..") || templateName.contains("/") || templateName.contains("\\") || templateName.startsWith(".");
        if (hasIllegalPath) {
            OutputStream out = response.getOutputStream();
            out.write(FILE_TEMPLATE_INVALID.getMessage().getBytes(StandardCharsets.UTF_8));
            out.flush();
            return;
        }
        String templatePath = "classpath:/templates/" + templateName;
        Resource resource = resourceLoader.getResource(templatePath);

        // 第一优先级：classpath 静态模板文件
        if (resource.exists()) {
            FileUtils.downloadTemplateFile(resourceLoader, response, templateName);
            return;
        }

        // 第二优先级：sys_temp_file 表（OSS 手动上传的模板）
        SysTempFileInfoVO sysTempFileInfoVO = sysTempFileService.detailByNameOrAlias(templateName, alias);
        if (sysTempFileInfoVO != null) {
            ResourceRef result = sysTempFileInfoVO.getUrl().getFirst();
            String filename = result.getOriginName();
            try (InputStream in = resourceService.readStream(result.getSceneCode(), result.getObjectKey());
                    OutputStream os = FileUtils.getOutputStream(response, filename)) {
                in.transferTo(os);
                os.flush();
            }
            return;
        }

        // 第三优先级：根据 @ExcelTemplate 注解动态生成空白模板
        // 优先用 alias 查找，alias 为空时再用 templateName 查找
        String lookupKey = (alias != null && !alias.isBlank()) ? alias : templateName;
        ExcelTemplateScanRegistry registry = excelTemplateScanRegistryProvider.getIfAvailable();
        Class<?> dtoClass = registry != null ? registry.getByAlias(lookupKey) : null;
        if (dtoClass != null) {
            String downloadFileName = lookupKey.endsWith(".xlsx") ? lookupKey : lookupKey + ".xlsx";
            try (OutputStream os = FileUtils.getOutputStream(response, downloadFileName)) {
                ExcelUtils.generateTemplate(dtoClass, os);
                os.flush();
            }
            return;
        }

        // 全部找不到：返回 404，通过响应头传递业务码，避免覆盖下载流的二进制内容
        String bizCode = FILE_NOT_EXISTS.getCodePrefixEnum().getPrefix() + FILE_NOT_EXISTS.getCode();
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("X-Biz-Code", bizCode);
        OutputStream out = response.getOutputStream();
        out.write(FILE_NOT_EXISTS.getMessage().getBytes(StandardCharsets.UTF_8));
        out.flush();
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

    @Override
    public ChallengeVO challenge() {
        HttpServletRequest request = HttpReqResUtil.getRequest();
        String requestId = Utils.generateSha256Id(Utils.generateAgentRequestId(request));
        int limit = Utils.getIntVal(SysConfigUtils.getConfValue(LoginConfigKeyConstant.REQUEST_LIMIT));
        Long requestCycle = Utils.getLongVal(SysConfigUtils.getConfValue(LoginConfigKeyConstant.REQUEST_CYCLE));

        if (limit != 0) {
            // 初始化请求限制
            redisCache.initializeLoginRequestLimit(requestId, requestCycle);
            Long cacheLimit = redisCache.countLoginRequestLimit(requestId);
            CommonResponseEnum.LOGIN_LIMIT.assertTrue(cacheLimit > limit);
        }

        // 根据request标识生成Sha256Id
        String secretKey = AESUtil.getRandomString(16);

        if (limit != 0) {
            redisCache.limitLoginRequest(requestId);
        }
        // 清除
        redisCache.clearLoginSecret(requestId);
        redisCache.putLoginSecret(requestId, secretKey, 60);
        return new ChallengeVO().setRequestId(requestId).setSecretKey(secretKey);
    }

}
