package com.sz.admin.system.business;

import com.sz.resource.model.ResourceRef;
import com.sz.resource.service.ResourceDownloadService;
import com.sz.resource.service.ResourceReferenceService;
import com.sz.resource.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceDownloadServiceTest {

    private static final Long RESOURCE_ID = 407693840630497281L;

    @Mock
    private ResourceReferenceService referenceService;

    @Mock
    private ResourceService resourceService;

    private ResourceDownloadService downloadService;

    @BeforeEach
    void setUp() {
        downloadService = new ResourceDownloadService(referenceService, resourceService);
    }

    @Test
    void downloadShouldStreamAttachmentAndApplySafeHeaders() throws Exception {
        ResourceRef resourceRef = resourceRef("teacher.attachment", "teacher-attachment/20260904/demo.pdf",
                "report/教师统计.pdf", "application/pdf");
        when(referenceService.findActiveReference(RESOURCE_ID)).thenReturn(Optional.of(resourceRef));
        when(resourceService.readStream(resourceRef.getSceneCode(), resourceRef.getObjectKey()))
                .thenReturn(new ByteArrayInputStream("download".getBytes(StandardCharsets.UTF_8)));

        var responseEntity = downloadService.download(RESOURCE_ID);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ((StreamingResponseBody) responseEntity.getBody()).writeTo(response.getOutputStream());

        assertThat(responseEntity.getHeaders().getContentDisposition().getType()).isEqualTo("attachment");
        assertThat(responseEntity.getHeaders().getContentDisposition().getFilename()).doesNotContain("/");
        assertThat(responseEntity.getHeaders().getCacheControl()).isEqualTo("no-store");
        assertThat(responseEntity.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getContentAsString(StandardCharsets.UTF_8)).isEqualTo("download");
    }

    @Test
    void previewShouldUseInlineDispositionForSupportedType() throws Exception {
        ResourceRef resourceRef = resourceRef("teacher.attachment", "teacher-attachment/20260904/demo.png",
                "demo.png", MediaType.IMAGE_PNG_VALUE);
        when(referenceService.findActiveReference(RESOURCE_ID)).thenReturn(Optional.of(resourceRef));
        when(resourceService.readStream(resourceRef.getSceneCode(), resourceRef.getObjectKey()))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));

        var responseEntity = downloadService.preview(RESOURCE_ID);

        assertThat(responseEntity.getHeaders().getContentDisposition().getType()).isEqualTo("inline");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
    }

    @Test
    void previewShouldRejectUnsupportedContentTypeBeforeReadingStream() {
        ResourceRef resourceRef = resourceRef("teacher.attachment", "teacher-attachment/20260904/demo.xlsx",
                "demo.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        when(referenceService.findActiveReference(RESOURCE_ID)).thenReturn(Optional.of(resourceRef));

        assertThatThrownBy(() -> downloadService.preview(RESOURCE_ID))
                .hasMessageContaining("不允许预览");
    }

    @Test
    void downloadShouldFallbackToOctetStreamForInvalidContentType() throws Exception {
        ResourceRef resourceRef = resourceRef("teacher.attachment", "teacher-attachment/20260904/demo.bin",
                "demo.bin", "invalid/content/type;charset==");
        when(referenceService.findActiveReference(RESOURCE_ID)).thenReturn(Optional.of(resourceRef));
        when(resourceService.readStream(resourceRef.getSceneCode(), resourceRef.getObjectKey()))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));

        var responseEntity = downloadService.download(RESOURCE_ID);

        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
    }

    private ResourceRef resourceRef(String sceneCode, String objectKey, String originName, String contentType) {
        ResourceRef resourceRef = new ResourceRef();
        resourceRef.setResourceId(RESOURCE_ID);
        resourceRef.setSceneCode(sceneCode);
        resourceRef.setObjectKey(objectKey);
        resourceRef.setOriginName(originName);
        resourceRef.setContentType(contentType);
        return resourceRef;
    }
}
