package com.sz.applet.miniBusiness.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 学校师生表 Mapper 接口
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Mapper
public interface SchoolUserMapper extends BaseMapper<SchoolUser> {

}