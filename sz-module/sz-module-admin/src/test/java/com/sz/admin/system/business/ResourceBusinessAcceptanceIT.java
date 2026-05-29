package com.sz.admin.system.business;

import com.sz.admin.system.controller.SysResourceController;
import com.sz.admin.system.mapper.SysResourceMapper;
import com.sz.admin.system.pojo.po.SysResource;
import com.sz.admin.system.service.SysResourceService;
import com.sz.admin.system.service.impl.SysResourceServiceImpl;
import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.enums.StorageTypeEnum;
import com.sz.resource.model.ResourceUploadResult;
import com.sz.resource.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResourceBusinessAcceptanceIT {

    @Test
    void uploadSplitsPathSegmentsAndPassesNamingKeyToService() throws IOException {
        SysResourceService service = mock(SysResourceService.class);
        SysResourceController controller = new SysResourceController(service);
        MockMultipartFile file = new MockMultipartFile("file", "logo.svg", "image/svg+xml", "<svg/>".getBytes(StandardCharsets.UTF_8));
        ResourceUploadResult uploadResult = ResourceUploadResult.builder().objectKey("providers/github.svg").build();
        when(service.upload(eq("sso.provider.logo"), eq("github"), eq(file), eq("client"), eq("logo"))).thenReturn(uploadResult);

        ResourceUploadResult result = controller.upload("sso.provider.logo", "github", "client,logo", file).getData();

        assertThat(result).isSameAs(uploadResult);
        verify(service).upload("sso.provider.logo", "github", file, "client", "logo");
    }

    @Test
    void batchUploadUploadsEveryMultipartFileWithSharedPathSegments() throws IOException {
        SysResourceService service = mock(SysResourceService.class);
        SysResourceController controller = new SysResourceController(service);
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockMultipartFile first = new MockMultipartFile("files", "a.png", "image/png", new byte[]{1});
        MockMultipartFile second = new MockMultipartFile("files", "b.png", "image/png", new byte[]{2});
        request.addFile(first);
        request.addFile(second);
        when(service.upload(eq("teacher.richtext"), eq(null), eq(first), eq("teacher"), eq("2026")))
                .thenReturn(ResourceUploadResult.builder().objectKey("richtext/a.png").build());
        when(service.upload(eq("teacher.richtext"), eq(null), eq(second), eq("teacher"), eq("2026")))
                .thenReturn(ResourceUploadResult.builder().objectKey("richtext/b.png").build());

        List<ResourceUploadResult> results = controller.batchUpload("teacher.richtext", "teacher,2026", request).getData();

        assertThat(results).extracting(ResourceUploadResult::getObjectKey).containsExactly("richtext/a.png", "richtext/b.png");
        verify(service).upload("teacher.richtext", null, first, "teacher", "2026");
        verify(service).upload("teacher.richtext", null, second, "teacher", "2026");
    }

    @Test
    void uploadWritesAuditResourceAndBackfillsGeneratedResourceId() throws IOException {
        ResourceService resourceService = mock(ResourceService.class);
        SysResourceMapper mapper = mock(SysResourceMapper.class);
        SysResourceServiceImpl service = new SysResourceServiceImpl(resourceService, resourceProperties(), mapper);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});
        ResourceUploadResult uploadResult = ResourceUploadResult.builder().objectKey("avatars/1001/avatar.png").originName("avatar.png").size(3L)
                .contentType("image/png").eTag("etag-1").build();
        when(resourceService.upload(eq("user.avatar"), eq("1001"), eq(file), eq("profile"))).thenReturn(uploadResult);
        doAnswer(invocation -> {
            SysResource resource = invocation.getArgument(0);
            resource.setId(88L);
            return 1;
        }).when(mapper).insert(any(SysResource.class));

        ResourceUploadResult result = service.upload("user.avatar", "1001", file, "profile");

        ArgumentCaptor<SysResource> resourceCaptor = ArgumentCaptor.forClass(SysResource.class);
        verify(mapper).insert(resourceCaptor.capture());
        SysResource saved = resourceCaptor.getValue();
        assertThat(saved.getSceneCode()).isEqualTo("user.avatar");
        assertThat(saved.getObjectKey()).isEqualTo("avatars/1001/avatar.png");
        assertThat(saved.getOriginName()).isEqualTo("avatar.png");
        assertThat(saved.getSize()).isEqualTo(3L);
        assertThat(saved.getContentType()).isEqualTo("image/png");
        assertThat(saved.getStorageType()).isEqualTo("LOCAL");
        assertThat(saved.getBizKey()).isEqualTo("1001");
        assertThat(saved.getDelFlag()).isEqualTo("F");
        assertThat(result.getResourceId()).isEqualTo(88L);
    }

    @Test
    void serveFileRejectsTraversalAndUnknownSceneBeforeReadingStorage() {
        ResourceService resourceService = mock(ResourceService.class);
        SysResourceServiceImpl service = new SysResourceServiceImpl(resourceService, resourceProperties(), mock(SysResourceMapper.class));

        MockHttpServletRequest traversal = new MockHttpServletRequest();
        traversal.setRequestURI("/api/admin/resource/file/avatars/%2e%2e/secret.png");
        MockHttpServletRequest unknown = new MockHttpServletRequest();
        unknown.setRequestURI("/api/admin/resource/file/missing/logo.png");

        assertThat(service.findServeFile("avatars", traversal).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(service.findServeFile("missing", unknown).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void serveFileDecodesSubPathChoosesMediaTypeAndStreamsStorageContent() throws IOException {
        ResourceService resourceService = mock(ResourceService.class);
        SysResourceServiceImpl service = new SysResourceServiceImpl(resourceService, resourceProperties(), mock(SysResourceMapper.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/resource/file/avatars/1/%E5%BE%AE%E4%BF%A1.png");
        when(resourceService.readStream("user.avatar", "avatars/1/微信.png"))
                .thenReturn(new ByteArrayInputStream("image-bytes".getBytes(StandardCharsets.UTF_8)));

        var response = service.findServeFile("avatars", request);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamingResponseBody body = response.getBody();
        body.writeTo(output);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("image-bytes");
        verify(resourceService).readStream("user.avatar", "avatars/1/微信.png");
    }

    private static ResourceProperties resourceProperties() {
        ResourceSceneConfig avatar = new ResourceSceneConfig();
        avatar.setCode("user.avatar");
        avatar.setType(StorageTypeEnum.LOCAL);
        avatar.setServeMode(ServeModeEnum.DIRECT);
        avatar.setPath("avatars/");
        avatar.setBaseUrl("http://127.0.0.1/api/admin/resource/file/avatars");
        ResourceProperties properties = new ResourceProperties();
        properties.setScenes(List.of(avatar));
        properties.validate();
        return properties;
    }
}
