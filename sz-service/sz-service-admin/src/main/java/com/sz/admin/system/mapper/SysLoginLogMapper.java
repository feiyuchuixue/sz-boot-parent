package com.sz.admin.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.system.pojo.po.SysLoginLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 登陆日志表 Mapper 接口
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    Long insertLoginLog(@Param("loginLog") SysLoginLog loginLog);

}