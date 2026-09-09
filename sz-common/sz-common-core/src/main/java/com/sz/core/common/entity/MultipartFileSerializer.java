package com.sz.core.common.entity;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 自定义MultipleFile序列化
 */
public class MultipartFileSerializer extends ValueSerializer<MultipartFile> {

    @Override
    public void serialize(MultipartFile value, JsonGenerator gen, SerializationContext serializers) {
        gen.writeString(value.getOriginalFilename());
    }
}
