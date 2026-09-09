package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.sysmessage.SysMessageListDTO;
import com.sz.admin.system.pojo.vo.sysmessage.MessageCountVO;
import com.sz.admin.system.pojo.vo.sysmessage.SysMessageVO;
import com.sz.admin.system.service.SysMessageService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 消息管理 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-04-21
 */
@Tag(name = "系统消息")
@RestController
@RequestMapping("sys-message")
@RequiredArgsConstructor
public class SysMessageController {

    private final SysMessageService sysMessageService;

    @Operation(summary = "我的未读消息数量")
    @GetMapping("/count")
    public ApiResult<MessageCountVO> countMyMessage() {
        return ApiResult.success(sysMessageService.countMyUnreadMessages());
    }

    @Operation(summary = "我的待办消息")
    @GetMapping("list/todo")
    public ApiResult<List<SysMessageVO>> listTodo() {
        SysMessageListDTO dto = new SysMessageListDTO("todo");
        return ApiResult.success(sysMessageService.list(dto));
    }

    @Operation(summary = "我的消息")
    @GetMapping("list/msg")
    public ApiResult<List<SysMessageVO>> listMsg() {
        SysMessageListDTO dto = new SysMessageListDTO("msg");
        return ApiResult.success(sysMessageService.list(dto));
    }

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<SysMessageVO>> list(SysMessageListDTO dto) {
        return ApiPageResult.success(sysMessageService.page(dto));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public ApiResult<SysMessageVO> detail(@PathVariable Object id) {
        return ApiResult.success(sysMessageService.detail(id));
    }
}
