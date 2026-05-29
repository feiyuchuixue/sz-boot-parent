package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SysDeptLeaderVO
 * 
 * @author sz
 * @since 2024/3/26 15:56
 * @version 1.0
 */
@Data
public class SysDeptLeaderVO {

    @Schema(description = "负责人信息数组")
    private List<LeaderInfoVO> leaderInfoVOS = new ArrayList<>();

    @Data
    public static class LeaderInfoVO {

        @Schema(description = "负责人Id")
        private Long id;

        @Schema(description = "负责人名称")
        private String nickname;
    }

}
