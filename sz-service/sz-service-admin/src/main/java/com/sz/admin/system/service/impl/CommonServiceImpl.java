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
import com.sz.redis.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final ResourceLoader resourceLoader;

    private final SysTempFileService sysTempFileService;

    private final OssClient ossClient;

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
        String confValue = SysConfigUtils.getConfValue("oss.accessMode");
        // 私有文件需要生成带签名的临时访问URL
        if ("private".equals(confValue)) {
            String finalBucket = getBucketFromUrl(fileUrl, "");
            String objectName = getObjectNameFromUrl(fileUrl);
            fileUrl = ossClient.getPrivateUrl(finalBucket, objectName);
        }
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
        CommonResponseEnum.NOT_EXISTS.message("URL 不能为空").assertTrue(url == null || url.isEmpty());
        String finalBucket = getBucketFromUrl(url, bucket);
        String objectName = getObjectNameFromUrl(url);
        log.info("bucket = {}, objectName = {}", finalBucket, objectName);
        return ossClient.getPrivateUrl(finalBucket, objectName);
    }

    @Override
    public void urlDownload(String url, HttpServletResponse response) throws IOException {
        CommonResponseEnum.NOT_EXISTS.message("URL 不能为空").assertTrue(url == null || url.isEmpty());
        String finalBucket = getBucketFromUrl(url, "");
        String objectName = getObjectNameFromUrl(url);
        String filename = getFilenameFromObjectName(objectName);
        String confValue = SysConfigUtils.getConfValue("oss.accessMode");
        String fileUrl = url;
        // 私有文件需要生成带签名的临时访问URL
        if ("private".equals(confValue)) {
            fileUrl = ossClient.getPrivateUrl(finalBucket, objectName);
        }
        try (InputStream in = new URL(fileUrl).openStream(); OutputStream os = FileUtils.getOutputStream(response, filename)) {
            in.transferTo(os);
            os.flush();
        }
    }

    /**
     * 根据 URL 获取 bucket： 1. 若是 http(s) 开头且能解析出 bucket，则返回 URL 中的 bucket 2. 否则返回
     * defaultBucket
     */
    public String getBucketFromUrl(String url, String defaultBucket) {
        String finalBucket = defaultBucket;

        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            int schemeEndIndex = url.indexOf("://");
            String noScheme = url.substring(schemeEndIndex + 3); // 去掉 "http://"/"https://"

            int firstSlashIndex = noScheme.indexOf('/');
            // firstSlashIndex < 0 或者刚好在最后一个字符，说明没有 path，无法解析
            CommonResponseEnum.INVALID.message("URL 格式不正确，无法解析出 bucket").assertTrue(firstSlashIndex < 0 || firstSlashIndex == noScheme.length() - 1);

            // path 形如：test/user/20241216/xxx.jpg
            String path = noScheme.substring(firstSlashIndex + 1);

            int secondSlashIndex = path.indexOf('/');
            // 没有第二个 /，说明没有 objectName 部分，也就无法切分出 bucket
            CommonResponseEnum.INVALID.message("URL 格式不正确，无法解析出 bucket").assertTrue(secondSlashIndex < 0);
            String bucketFromUrl = path.substring(0, secondSlashIndex);
            if (finalBucket == null || finalBucket.isEmpty()) {
                finalBucket = bucketFromUrl;
            }
        }
        return finalBucket;
    }

    /**
     * 根据 URL 获取 objectName： 1. 若是 http(s) 开头，则解析出 path 中 bucket 后面的部分作为 objectName
     * 2. 否则直接把 url 当成 objectName
     */
    public String getObjectNameFromUrl(String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            int schemeEndIndex = url.indexOf("://");
            String noScheme = url.substring(schemeEndIndex + 3); // 去掉 "http://"/"https://"

            int firstSlashIndex = noScheme.indexOf('/');
            CommonResponseEnum.INVALID.message("URL 格式不正确，无法解析出 objectName").assertTrue(firstSlashIndex < 0 || firstSlashIndex == noScheme.length() - 1);

            String path = noScheme.substring(firstSlashIndex + 1); // 去掉第一个 "/"

            // path 形如：test/user/20241216/xxx.jpg
            int secondSlashIndex = path.indexOf('/');
            CommonResponseEnum.INVALID.message("URL 格式不正确，无法解析出 objectName").assertTrue(secondSlashIndex < 0);

            // 去掉 bucket 后面的 "/"
            return path.substring(secondSlashIndex + 1);
        } else {
            // 非 http(s) 开头，直接认为是 objectName
            return url;
        }
    }

    public String getFilenameFromObjectName(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return objectName;
        }
        int lastSlashIndex = objectName.lastIndexOf('/');
        if (lastSlashIndex < 0 || lastSlashIndex == objectName.length() - 1) {
            // 没有 "/" 或 "/" 在最后（类似 "xxx/"），直接返回原字符串
            return objectName;
        }
        return objectName.substring(lastSlashIndex + 1);
    }

}
