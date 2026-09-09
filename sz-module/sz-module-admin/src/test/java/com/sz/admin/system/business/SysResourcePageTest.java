package com.sz.admin.system.business;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.admin.system.mapper.SysResourceMapper;
import com.sz.admin.system.controller.SysResourceController;
import com.sz.core.common.configuration.JacksonConfiguration;
import tools.jackson.databind.json.JsonMapper;
import com.sz.admin.system.pojo.dto.sysresource.SysResourceListDTO;
import com.sz.admin.system.pojo.vo.sysresource.SysResourceVO;
import com.sz.admin.system.service.impl.SysResourceServiceImpl;
import com.sz.resource.config.ResourceProperties;
import com.sz.resource.config.ResourceSceneConfig;
import com.sz.resource.enums.ServeModeEnum;
import com.sz.resource.service.ResourceService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SysResourcePageTest {

    @Test
    void pageIncludesExplicitAccessModeEvenWithoutPublicUrl() {
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode("teacher.attachment");
        scene.setPath("attachments");
        ResourceProperties properties = new ResourceProperties();
        properties.setScenes(List.of(scene));
        properties.validate();
        ResourceService resources = mock(ResourceService.class);
        SysResourceServiceImpl service = spy(new SysResourceServiceImpl(resources, properties, mock(SysResourceMapper.class)));
        SysResourceVO row = new SysResourceVO();
        row.setSceneCode(scene.getCode());
        row.setObjectKey("attachments/report.pdf");
        Page<SysResourceVO> page = new Page<>(1, 10, 1);
        page.setRecords(List.of(row));
        doReturn(page).when(service).pageAs(any(Page.class), any(QueryWrapper.class), eq(SysResourceVO.class));

        SysResourceVO result = service.page(new SysResourceListDTO()).getRows().getFirst();

        assertThat(result.getServeMode()).isEqualTo(ServeModeEnum.PROTECTED);
        assertThat(result.getSceneName()).isEqualTo("teacher.attachment");
        assertThat(result.getAccessUrl()).isNull();
        verifyNoInteractions(resources);
    }

    @Test
    void sceneOptionsExposeOnlyCodeAndNameWithCodeFallback() {
        ResourceProperties properties = mock(ResourceProperties.class);
        ResourceSceneConfig named = scene("teacher.attachment", "教师附件", ServeModeEnum.PROTECTED);
        ResourceSceneConfig unnamed = scene("legacy.file", null, ServeModeEnum.DIRECT);
        named.setBucket("internal-bucket");
        when(properties.getSceneMap()).thenReturn(Map.of(named.getCode(), named, unnamed.getCode(), unnamed));
        SysResourceServiceImpl service = new SysResourceServiceImpl(mock(ResourceService.class), properties, mock(SysResourceMapper.class));

        assertThat(new SysResourceController(service).scenes().getData())
                .containsExactlyInAnyOrderEntriesOf(Map.of("teacher.attachment", "教师附件", "legacy.file", "legacy.file"));
    }

    @Test
    void pageDistinguishesPublicSignedProtectedAndMissingConfiguration() {
        ResourceProperties properties = mock(ResourceProperties.class);
        when(properties.getSceneMap()).thenReturn(Map.of(
                "public", scene("public", "公开图片", ServeModeEnum.DIRECT),
                "signed", scene("signed", "签名图片", ServeModeEnum.PRESIGNED),
                "protected", scene("protected", "业务附件", ServeModeEnum.PROTECTED)));
        ResourceService resources = mock(ResourceService.class);
        when(resources.resolveUrl("public", "files/public")).thenReturn("/files/public");
        when(resources.resolveUrl("signed", "files/signed")).thenReturn("https://example.com/signed?signature=test");
        SysResourceServiceImpl service = spy(new SysResourceServiceImpl(resources, properties, mock(SysResourceMapper.class)));
        List<SysResourceVO> rows = List.of(row("public"), row("signed"), row("protected"), row("deleted"));
        Page<SysResourceVO> page = new Page<>(1, 10, rows.size());
        page.setRecords(rows);
        doReturn(page).when(service).pageAs(any(Page.class), any(QueryWrapper.class), eq(SysResourceVO.class));

        List<SysResourceVO> results = service.page(new SysResourceListDTO()).getRows();

        assertThat(results).extracting(SysResourceVO::getServeMode)
                .containsExactly(ServeModeEnum.DIRECT, ServeModeEnum.PRESIGNED, ServeModeEnum.PROTECTED, null);
        assertThat(results).extracting(SysResourceVO::getSceneName).containsExactly("公开图片", "签名图片", "业务附件", "deleted");
        assertThat(results).extracting(SysResourceVO::getAccessUrl)
                .containsExactly("/files/public", "https://example.com/signed?signature=test", null, null);
        verify(resources, never()).resolveUrl(eq("protected"), any());
        verify(resources, never()).resolveUrl(eq("deleted"), any());
    }

    @Test
    void failedUrlDoesNotMasqueradeAsProtectedOrBreakTheWholePage() {
        ResourceProperties properties = mock(ResourceProperties.class);
        when(properties.getSceneMap()).thenReturn(Map.of("signed", scene("signed", "临时图片", ServeModeEnum.PRESIGNED)));
        ResourceService resources = mock(ResourceService.class);
        when(resources.resolveUrl("signed", "files/signed")).thenThrow(new IllegalStateException("signing unavailable"));
        SysResourceServiceImpl service = spy(new SysResourceServiceImpl(resources, properties, mock(SysResourceMapper.class)));
        Page<SysResourceVO> page = new Page<>(1, 10, 2);
        page.setRecords(List.of(row("signed"), row("unknown")));
        doReturn(page).when(service).pageAs(any(Page.class), any(QueryWrapper.class), eq(SysResourceVO.class));

        List<SysResourceVO> results = service.page(new SysResourceListDTO()).getRows();

        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getServeMode()).isEqualTo(ServeModeEnum.PRESIGNED);
        assertThat(results.getFirst().getAccessUrl()).isNull();
        assertThat(results.get(1).getServeMode()).isNull();
    }

    private static ResourceSceneConfig scene(String code, String name, ServeModeEnum mode) {
        ResourceSceneConfig scene = new ResourceSceneConfig();
        scene.setCode(code);
        scene.setName(name);
        scene.setServeMode(mode);
        return scene;
    }

    @Test
    void resourceMetadataMatchesConfiguredJsonContract() {
        JsonMapper.Builder builder = JsonMapper.builder();
        new JacksonConfiguration().szJacksonCustomizer().customize(builder);
        JsonMapper mapper = builder.build();
        SysResourceVO row = row("missing.scene");
        row.setETag("etag-value");
        var json = mapper.readTree(mapper.writeValueAsString(row));

        assertThat(json.path("serveMode").asString()).isEmpty();
        assertThat(json.path("bizKey").asString()).isEmpty();
        assertThat(json.path("eTag").asString()).isEqualTo("etag-value");
    }

    private static SysResourceVO row(String code) {
        SysResourceVO row = new SysResourceVO();
        row.setSceneCode(code);
        row.setObjectKey("files/" + code);
        return row;
    }
}
