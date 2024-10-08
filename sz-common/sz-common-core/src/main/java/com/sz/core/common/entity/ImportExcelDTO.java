package com.sz.core.common.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName ImportExcelDTO
 * @Author sz
 * @Date 2024/10/8 17:21
 * @Version 1.0
 */
@Data
public class ImportExcelDTO {

    private MultipartFile file;

    private Boolean isCover;

}
