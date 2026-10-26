package com.sz.admin.system.service;

import com.sz.admin.system.pojo.dto.sysresource.SysResourceListDTO;
import com.sz.admin.system.pojo.po.SysResource;
import com.sz.admin.system.pojo.vo.sysresource.SysResourceVO;
import com.sz.core.common.entity.PageResult;
import com.sz.resource.model.ResourceUploadResult;
import com.sz.resource.service.ResourceReferenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface SysResourceService extends ResourceReferenceService {

    ResourceUploadResult upload(String sceneCode, String namingKey, MultipartFile file, String... pathSegments) throws IOException;

    /**
     * 本地开发文件入口的流式读取，只允许唯一匹配的 LOCAL DIRECT 场景；入口注册由开发文件服务开关控制。
     */
    ResponseEntity<StreamingResponseBody> findServeFile(String sceneDir, HttpServletRequest request);

    PageResult<SysResourceVO> page(SysResourceListDTO dto);

    Map<String, String> getSceneNames();

    Optional<SysResource> findActiveById(Long resourceId);

}
