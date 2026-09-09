package com.sz.admin.system.service;

import com.sz.admin.system.pojo.dto.sysresource.SysResourceListDTO;
import com.sz.admin.system.pojo.po.SysResource;
import com.sz.admin.system.pojo.vo.sysresource.SysResourceVO;
import com.sz.core.common.entity.PageResult;
import com.sz.resource.model.ResourceRef;
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

    ResponseEntity<StreamingResponseBody> findServeFile(String sceneDir, HttpServletRequest request);

    PageResult<SysResourceVO> page(SysResourceListDTO dto);

    Map<String, String> getSceneNames();

    Optional<SysResource> findActiveById(Long resourceId);

}
