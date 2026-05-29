package com.sz.generator.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator path candidates.
 *
 * @author sz
 */
@Data
@Schema(description = "代码生成器路径候选")
public class GeneratorPathOptionsVO {

    @Schema(description = "默认后端路径")
    private String defaultApiPath;

    @Schema(description = "默认前端路径")
    private String defaultWebPath;

    @Schema(description = "后端路径候选")
    private List<PathOption> apiOptions = new ArrayList<>();

    @Schema(description = "前端路径候选")
    private List<PathOption> webOptions = new ArrayList<>();

    @Data
    @Schema(description = "路径候选项")
    public static class PathOption {

        @Schema(description = "显示名称")
        private String label;

        @Schema(description = "路径")
        private String path;

        public PathOption(String label, String path) {
            this.label = label;
            this.path = path;
        }
    }
}
