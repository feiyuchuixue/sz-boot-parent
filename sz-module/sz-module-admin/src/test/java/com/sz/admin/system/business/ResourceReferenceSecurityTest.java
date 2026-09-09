package com.sz.admin.system.business;

import com.sz.admin.system.mapper.SysResourceMapper;
import com.sz.admin.system.pojo.po.SysResource;
import com.sz.admin.system.service.impl.SysResourceServiceImpl;
import com.sz.resource.config.ResourceProperties;
import com.sz.resource.model.ResourceRef;
import com.sz.resource.service.ResourceService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ResourceReferenceSecurityTest {

    @Test
    void createRebuildsSubmittedReferenceFromTrustedResourceMetadata() {
        SysResourceServiceImpl service = serviceWith(resource(101L, "teacher.attachment", 7L));
        ResourceRef submitted = reference(101L);
        submitted.setSceneCode("forged.scene");
        submitted.setObjectKey("forged/object.exe");
        submitted.setOriginName("forged.exe");
        submitted.setContentType("application/x-msdownload");
        submitted.setAccessUrl("https://attacker.example/file");

        List<ResourceRef> result = service.normalizeForCreate(List.of(submitted), "teacher.attachment", 7L);

        assertThat(result).singleElement().satisfies(reference -> {
            assertThat(reference.getResourceId()).isEqualTo(101L);
            assertThat(reference.getSceneCode()).isEqualTo("teacher.attachment");
            assertThat(reference.getObjectKey()).isEqualTo("teacher/report.pdf");
            assertThat(reference.getOriginName()).isEqualTo("report.pdf");
            assertThat(reference.getContentType()).isEqualTo("application/pdf");
            assertThat(reference.getAccessUrl()).isNull();
        });
    }

    @Test
    void createRejectsResourcesUploadedByAnotherUser() {
        SysResourceServiceImpl service = serviceWith(resource(101L, "teacher.attachment", 7L));

        assertThatThrownBy(() -> service.normalizeForCreate(List.of(reference(101L)), "teacher.attachment", 8L))
                .hasMessageContaining("新增资源必须由当前用户上传");
    }

    @Test
    void updateKeepsExistingReferenceWithoutRequiringOriginalUploader() {
        SysResourceServiceImpl service = serviceWith(resource(101L, "teacher.attachment", 7L));

        List<ResourceRef> result = service.normalizeForUpdate(List.of(reference(101L)), List.of(reference(101L)), "teacher.attachment", 8L);

        assertThat(result).extracting(ResourceRef::getResourceId).containsExactly(101L);
    }

    @Test
    void normalizationRejectsSceneMismatchAndDeduplicatesResourceIds() {
        SysResourceServiceImpl service = serviceWith(resource(101L, "teacher.attachment", 7L));

        assertThat(service.normalizeForCreate(List.of(reference(101L), reference(101L)), "teacher.attachment", 7L))
                .extracting(ResourceRef::getResourceId).containsExactly(101L);
        assertThatThrownBy(() -> service.normalizeForCreate(List.of(reference(101L)), "template.excel", 7L))
                .hasMessageContaining("资源场景不匹配");
    }

    private static SysResourceServiceImpl serviceWith(SysResource resource) {
        SysResourceServiceImpl service = spy(new SysResourceServiceImpl(mock(ResourceService.class), mock(ResourceProperties.class),
                mock(SysResourceMapper.class)));
        doReturn(Optional.of(resource)).when(service).findActiveById(resource.getId());
        return service;
    }

    private static SysResource resource(Long id, String sceneCode, Long createId) {
        SysResource resource = new SysResource();
        resource.setId(id);
        resource.setSceneCode(sceneCode);
        resource.setObjectKey("teacher/report.pdf");
        resource.setOriginName("report.pdf");
        resource.setContentType("application/pdf");
        resource.setCreateId(createId);
        resource.setDelFlag("F");
        return resource;
    }

    private static ResourceRef reference(Long resourceId) {
        ResourceRef reference = new ResourceRef();
        reference.setResourceId(resourceId);
        return reference;
    }
}
