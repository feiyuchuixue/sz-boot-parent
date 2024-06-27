package com.sz.admin.system.pojo.vo.sysdatascope;

import com.sz.admin.system.pojo.vo.sysdept.SysDeptVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SysDataScopeVO
 * @Author sz
 * @Date 2024/6/27 13:26
 * @Version 1.0
 */
@Data
@Schema(description = "SysDataScope返回vo")
public class SysDataScopeVO {

    {
        selectIds = new ArrayList<>();
    }

    @Schema(description = "部门列表")
    private List<SysDeptVO> deptLists;

    @Schema(description = "选中的部门id")
    private List<String> selectIds;

}
