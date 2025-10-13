package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysLoginLogMapper;
import com.sz.admin.system.pojo.dto.SysLoginLogCreateDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogListDTO;
import com.sz.admin.system.pojo.dto.SysLoginLogUpdateDTO;
import com.sz.admin.system.pojo.po.SysLoginLog;
import com.sz.admin.system.pojo.vo.SysLoginLogVO;
import com.sz.admin.system.service.SysLoginLogService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.ip.IpUtils;
import com.sz.core.util.*;
import com.sz.excel.utils.ExcelUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 登陆日志表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private static final UserAgentAnalyzer USER_AGENT_ANALYZER = UserAgentAnalyzer.newBuilder().dropTests().build();

    @Override
    public void create(SysLoginLogCreateDTO dto) {
        SysLoginLog sysLoginLog = BeanCopyUtils.copy(dto, SysLoginLog.class);
        save(sysLoginLog);
    }

    @Override
    public void update(SysLoginLogUpdateDTO dto) {
        SysLoginLog sysLoginLog = BeanCopyUtils.copy(dto, SysLoginLog.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysLoginLog::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(sysLoginLog);
    }

    @Override
    public PageResult<SysLoginLogVO> page(SysLoginLogListDTO dto) {
        Page<SysLoginLogVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysLoginLogVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysLoginLogVO> list(SysLoginLogListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysLoginLogVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SysLoginLogVO detail(Object id) {
        SysLoginLog sysLoginLog = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysLoginLog);
        return BeanCopyUtils.copy(sysLoginLog, SysLoginLogVO.class);
    }

    @SneakyThrows
    @Override
    public void exportExcel(SysLoginLogListDTO dto, HttpServletResponse response) {
        List<SysLoginLogVO> list = list(dto);
        String fileName = "登陆日志表模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "登陆日志表", SysLoginLogVO.class, os);
    }

    /**
     * 异步记录登陆日志
     */
    @Override
    public void recordLoginLog(String username, String status, String msg) {
        try {
            // 在主线程中获取所有请求信息
            HttpServletRequest request = HttpReqResUtil.getRequest();
            String ipAddress = HttpReqResUtil.getIpAddress(request);
            String userAgentString = request.getHeader("User-Agent");
            String realAddressByIP = IpUtils.getRealAddressByIP(ipAddress);
            // 使用虚拟线程进行异步提交
            Thread.startVirtualThread(() -> {
                try {
                    UserAgent.ImmutableUserAgent agent = USER_AGENT_ANALYZER.parse(userAgentString);
                    SysLoginLog log = new SysLoginLog();
                    log.setUserName(username);
                    log.setLoginStatus(status);
                    log.setLoginTime(LocalDateTime.now());
                    log.setMsg(msg);
                    log.setIpAddress(ipAddress);
                    log.setLoginLocation(realAddressByIP);
                    log.setBrowser(agent.getValue(UserAgent.AGENT_NAME));
                    log.setOs(agent.getValue(UserAgent.OPERATING_SYSTEM_NAME));
                    this.mapper.insertLoginLog(log);
                } catch (Exception e) {
                    log.error("登陆日志记录异常-用户名:{}, 状态:{}, 错误:{}", username, status, e.getMessage());
                }
            });
        } catch (Exception e) {
            // 获取请求信息失败记录基本日志信息
            log.error("获取登录日志请求信息失败-用户名:{}, 状态:{}, 错误:{}", username, status, e.getMessage());
            SysLoginLog log = new SysLoginLog();
            log.setUserName(username);
            log.setLoginStatus(status);
            log.setLoginTime(LocalDateTime.now());
            log.setMsg(msg);
            this.mapper.insertLoginLog(log);
        }
    }

    private static QueryWrapper buildQueryWrapper(SysLoginLogListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysLoginLog.class);
        if (Utils.isNotNull(dto.getUserName())) {
            wrapper.like(SysLoginLog::getUserName, dto.getUserName());
        }
        if (Utils.isNotNull(dto.getLoginStatus())) {
            wrapper.eq(SysLoginLog::getLoginStatus, dto.getLoginStatus());
        }
        if (Utils.isNotNull(dto.getLoginTimeStart()) && Utils.isNotNull(dto.getLoginTimeEnd())) {
            wrapper.between(SysLoginLog::getLoginTime, dto.getLoginTimeStart(), dto.getLoginTimeEnd());
        }
        wrapper.orderBy(SysLoginLog::getLoginTime).desc();
        return wrapper;
    }
}