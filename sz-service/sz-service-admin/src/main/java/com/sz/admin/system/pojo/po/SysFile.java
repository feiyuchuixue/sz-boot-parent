package com.sz.admin.system.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author sz
 * @since 2023-08-31
 */
@Data
@Table("sys_file")
@Schema(description = "文件表")
public class SysFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "文件路径")
    private String path;

    @Schema(description = "文件大小")
    private String size;

    @Schema(description = "文件url")
    private String url;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
