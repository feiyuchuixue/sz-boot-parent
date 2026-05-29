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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
            String fileUrl = resourceService.resolveUrl(result.getSceneCode(), result.getObjectKey());
            String filename = result.getOriginName();
            try (InputStream in = new URL(fileUrl).openStream(); OutputStream os = FileUtils.getOutputStream(response, filename)) {
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
        response.setHeader("X-Biz-Message", FILE_NOT_EXISTS.getMessage());
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

    @Override
    public void urlDownload(String url, HttpServletResponse response) throws IOException {
        CommonResponseEnum.NOT_EXISTS.message("URL 不能为空").assertTrue(url == null || url.isEmpty());
        URL parsedUrl;
        try {
            parsedUrl = new URL(url);
        } catch (MalformedURLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL格式错误");
            return;
        }
        String protocol = parsedUrl.getProtocol();
        if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "非法的URL协议");
            return;
        }
        String filename = getFilenameFromObjectName(parsedUrl.getPath());
        String accessUrl;
        try {
            URI uri = new URI(parsedUrl.getProtocol(), parsedUrl.getUserInfo(), parsedUrl.getHost(), parsedUrl.getPort(), parsedUrl.getPath(),
                    parsedUrl.getQuery(), null);
            accessUrl = uri.toASCIIString();
        } catch (URISyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL格式错误");
            return;
        }
        try (InputStream in = URI.create(accessUrl).toURL().openStream(); OutputStream os = FileUtils.getOutputStream(response, filename)) {
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
