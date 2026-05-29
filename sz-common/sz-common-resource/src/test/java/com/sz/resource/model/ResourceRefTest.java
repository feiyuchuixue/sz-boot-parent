package com.sz.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceRefTest {

    @Test
    void resourceRefStoresStableReferenceFieldsAndKeepsAccessUrlReadOnly() throws NoSuchFieldException {
        ResourceRef ref = new ResourceRef();
        ref.setResourceId(1001L);
        ref.setSceneCode("teacher_avatar");
        ref.setObjectKey("teacher/1/avatar.png");
        ref.setOriginName("avatar.png");
        ref.setContentType("image/png");
        ref.setAccessUrl("https://cdn.example.com/teacher/1/avatar.png");

        assertThat(ref.getResourceId()).isEqualTo(1001L);
        assertThat(ref.getSceneCode()).isEqualTo("teacher_avatar");
        assertThat(ref.getObjectKey()).isEqualTo("teacher/1/avatar.png");
        assertThat(ref.getOriginName()).isEqualTo("avatar.png");
        assertThat(ref.getContentType()).isEqualTo("image/png");

        Field accessUrl = ResourceRef.class.getDeclaredField("accessUrl");
        JsonProperty jsonProperty = accessUrl.getAnnotation(JsonProperty.class);
        assertThat(jsonProperty.access()).isEqualTo(JsonProperty.Access.READ_ONLY);
    }
}
