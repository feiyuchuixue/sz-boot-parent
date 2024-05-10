package com.sz.www.sysregion.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;

import com.sz.core.common.entity.PageResult;
import com.sz.www.sysregion.service.SysRegionService;
import com.sz.www.sysregion.pojo.dto.SysRegionListDTO;
import com.sz.www.sysregion.pojo.vo.SysRegionVO;

/**
 * <p>
 * 测试网站 Controller
 * </p>
 *
 * @author sz
 * @since 2024-04-25
 */
@Tag(name =  "测试网站")
@RestController
@RequestMapping("www")
@RequiredArgsConstructor
public class SysRegionController  {

    private final SysRegionService sysRegionService;
    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<SysRegionVO>> list(SysRegionListDTO dto) {
        return ApiPageResult.success(sysRegionService.page(dto));
    }

}
