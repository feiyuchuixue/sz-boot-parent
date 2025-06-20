package com.sz.ssoadmin.ssouser.service;

import com.mybatisflex.core.service.IService;
import com.sz.ssoadmin.ssouser.pojo.po.SsoUser;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserCreateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserUpdateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserListDTO;
import com.sz.ssoadmin.ssouser.pojo.vo.SsoUserVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 平台用户表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
public interface SsoUserService extends IService<SsoUser> {

    void create(SsoUserCreateDTO dto);

    void update(SsoUserUpdateDTO dto);

    PageResult<SsoUserVO> page(SsoUserListDTO dto);

    List<SsoUserVO> list(SsoUserListDTO dto);

    void remove(SelectIdsDTO dto);

    SsoUserVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(SsoUserListDTO dto, HttpServletResponse response);

    void resetPassword(Long userId);
}