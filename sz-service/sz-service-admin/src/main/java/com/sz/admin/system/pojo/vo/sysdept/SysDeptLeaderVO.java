package com.sz.admin.system.pojo.vo.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SysDeptLeaderVO
 * @Author sz
 * @Date 2024/3/26 15:56
 * @Version 1.0
 */
@Data
public class SysDeptLeaderVO {

    {
        leaderInfoVOS = new ArrayList<>();
    }

    /*
     * @Schema(description = "选中的负责人id数组") private List<Integer> selectIds;
     */

    @Schema(description = "负责人信息数组")
    private List<LeaderInfoVO> leaderInfoVOS;

    @Data
    public static class LeaderInfoVO {

        @Schema(description = "负责人Id")
        private Long id;

        @Schema(description = "负责人名称")
        private String nickname;
    }

}
