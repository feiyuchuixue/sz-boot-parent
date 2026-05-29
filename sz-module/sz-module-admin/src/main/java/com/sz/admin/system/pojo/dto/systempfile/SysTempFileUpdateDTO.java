package com.sz.admin.system.pojo.dto.systempfile;

import com.mybatisflex.annotation.Column;
import com.sz.db.handler.Jackson3TypeHandler;
import com.sz.resource.model.ResourceRef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SysTempFile修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile修改DTO")
public class SysTempFileUpdateDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "文件ID, sys_resource id")
    private Long sysFileId;

    @Schema(description = "模版名")
    private String tempName;

    @Schema(description = "地址")
    @Column(typeHandler = Jackson3TypeHandler.class)
    private List<ResourceRef> url;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "标识")
    private String alias;

}