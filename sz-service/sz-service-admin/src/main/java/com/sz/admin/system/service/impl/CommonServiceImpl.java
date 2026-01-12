package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.vo.common.ChallengeVO;
import com.sz.admin.system.pojo.vo.common.SelectorVO;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileInfoVO;
import com.sz.admin.system.service.*;
import com.sz.core.common.entity.UploadResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.*;
import com.sz.oss.OssClient;
import com.sz.oss.OssProperties;
import com.sz.redis.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.sz.core.common.enums.CommonResponseEnum.FILE_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final ResourceLoader resourceLoader;

    private final SysTempFileService sysTempFileService;

    private final OssClient ossClient;

    private final OssProperties ossProperties;

    private final SysUserService sysUserService;

    private final SysDeptService sysDeptService;

    private final SysRoleService sysRoleService;

    private final RedisCache redisCache;

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

        UploadResult result = sysTempFileInfoVO.getUrl().getFirst();
        String fileUrl = result.getUrl();
        String filename = result.getFilename();
        long size = result.getSize();
        if (size > 0)
            response.setContentLengthLong(size);
        try (InputStream in = new URL(fileUrl).openStream(); OutputStream os = FileUtils.getOutputStream(response, filename)) {
            in.transferTo(os);
            os.flush();
        }

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
        int limit = Utils.getIntVal(SysConfigUtils.getConfValue("sys.login.requestLimit"));
        Long requestCycle = Utils.getLongVal(SysConfigUtils.getConfValue("sys.login.requestCycle"));

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

    @Override
    public String ossPrivateUrl(String bucket, String url) {
        if (bucket == null || bucket.isEmpty()) {
            bucket = ossProperties.getBucketName();
        }
        try {
            String objectName;
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // 完整 URL，解析 objectName
                java.net.URI uri = java.net.URI.create(url);
                String path = uri.getPath();
                String prefix = "/" + bucket + "/";
                CommonResponseEnum.NOT_EXISTS.message("无效资源地址").assertFalse(path.startsWith(prefix));
                objectName = path.substring(prefix.length());
            } else {
                // 直接传 objectName
                objectName = url;
            }
            return ossClient.getPrivateUrl(objectName);
        } catch (Exception e) {
            CommonResponseEnum.UNKNOWN.message("生成私有访问地址失败").assertTrue(true);
        }
        return "";
    }

}
