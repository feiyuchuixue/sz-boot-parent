package com.sz.resource.service;

import com.sz.resource.enums.ResourceAccessResponseEnum;
import com.sz.resource.model.ResourceRef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * 受保护资源的统一流式下载与预览服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceDownloadService {

    private static final Set<String> PREVIEW_CONTENT_TYPES = Set.of(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp",
            MediaType.IMAGE_GIF_VALUE, MediaType.APPLICATION_PDF_VALUE);

    private final ResourceReferenceService referenceService;

    private final ResourceService resourceService;

    public ResponseEntity<StreamingResponseBody> download(Long resourceId) {
        return open(resourceId, false);
    }

    public ResponseEntity<StreamingResponseBody> preview(Long resourceId) {
        return open(resourceId, true);
    }

    private ResponseEntity<StreamingResponseBody> open(Long resourceId, boolean preview) {
        ResourceRef resource = referenceService.findActiveReference(resourceId)
                .orElseThrow(() -> ResourceAccessResponseEnum.RESOURCE_NOT_FOUND.newException());

        MediaType mediaType = resolveMediaType(resource.getContentType(), preview);
        InputStream inputStream;
        try {
            inputStream = resourceService.readStream(resource.getSceneCode(), resource.getObjectKey());
        } catch (IOException | RuntimeException exception) {
            log.error("[ResourceAccess] 资源读取失败，resourceId={} sceneCode={} objectKey={}", resource.getResourceId(), resource.getSceneCode(),
                    resource.getObjectKey(), exception);
            throw ResourceAccessResponseEnum.STORAGE_READ_FAILED.newException();
        }

        StreamingResponseBody body = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }
        };
        ContentDisposition disposition = contentDisposition(preview, resource.getOriginName());
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff").cacheControl(CacheControl.noStore()).body(body);
    }

    private MediaType resolveMediaType(String contentType, boolean preview) {
        String normalized = contentType == null ? "" : contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        if (preview && !PREVIEW_CONTENT_TYPES.contains(normalized)) {
            throw ResourceAccessResponseEnum.PREVIEW_NOT_ALLOWED.newException();
        }
        try {
            return normalized.isEmpty() ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(normalized);
        } catch (IllegalArgumentException exception) {
            if (preview) {
                throw ResourceAccessResponseEnum.PREVIEW_NOT_ALLOWED.newException();
            }
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private ContentDisposition contentDisposition(boolean preview, String originName) {
        ContentDisposition.Builder builder = preview ? ContentDisposition.inline() : ContentDisposition.attachment();
        return builder.filename(sanitizeFilename(originName), StandardCharsets.UTF_8).build();
    }

    private String sanitizeFilename(String originName) {
        return originName == null || originName.isBlank() ? "download" : originName.replaceAll("[\\r\\n\\\\/:]", "_");
    }
}
