package com.sz.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportExcelDTO {

    private MultipartFile file;

    @Schema(description = "是否数据覆盖", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isCover;

}
