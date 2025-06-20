package com.sz.ssoadmin.ssouser.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.core.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.ssoadmin.ssouser.service.SsoUserService;
import com.sz.ssoadmin.ssouser.pojo.po.SsoUser;
import com.sz.ssoadmin.ssouser.mapper.SsoUserMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryChain;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;

import java.io.Serializable;
import java.util.List;

import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserCreateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserUpdateDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserListDTO;
import com.sz.ssoadmin.ssouser.pojo.dto.SsoUserImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;

import java.io.OutputStream;

import jakarta.servlet.http.HttpServletResponse;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.ssoadmin.ssouser.pojo.vo.SsoUserVO;

/**
 * <p>
 * 平台用户表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-06-17
 */
@Service
@RequiredArgsConstructor
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {

    @Override
    public void create(SsoUserCreateDTO dto) {
        SsoUser ssoUser = BeanCopyUtils.copy(dto, SsoUser.class);
        long count;
        // 唯一性校验
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getUsername, dto.getUsername()).count();
        CommonResponseEnum.EXISTS.message("username已存在").assertTrue(count > 0);
        // 唯一性校验
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getPhone, dto.getPhone()).count();
        CommonResponseEnum.EXISTS.message("phone已存在").assertTrue(count > 0);
        // 唯一性校验
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getEmail, dto.getEmail()).count();
        CommonResponseEnum.EXISTS.message("email已存在").assertTrue(count > 0);
        ssoUser.setNeedChangePassword("T");
        ssoUser.setPasswordHash(getEncoderPwd(getInitPassword()));
        save(ssoUser);
    }

    @Override
    public void update(SsoUserUpdateDTO dto) {
        SsoUser ssoUser = BeanCopyUtils.copy(dto, SsoUser.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SsoUser::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        // 唯一性校验
        long count;
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getUsername, dto.getUsername()).ne(SsoUser::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("username已存在").assertTrue(count > 0);
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getPhone, dto.getPhone()).ne(SsoUser::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("phone已存在").assertTrue(count > 0);
        count = QueryChain.of(SsoUser.class).eq(SsoUser::getEmail, dto.getEmail()).ne(SsoUser::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("email已存在").assertTrue(count > 0);
        saveOrUpdate(ssoUser);
    }

    @Override
    public PageResult<SsoUserVO> page(SsoUserListDTO dto) {
        Page<SsoUserVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SsoUserVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SsoUserVO> list(SsoUserListDTO dto) {
        return listAs(buildQueryWrapper(dto), SsoUserVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SsoUserVO detail(Object id) {
        SsoUser ssoUser = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(ssoUser);
        return BeanCopyUtils.copy(ssoUser, SsoUserVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<SsoUserImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), SsoUserImportDTO.class, true);
        List<SsoUserImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(SsoUserListDTO dto, HttpServletResponse response) {
        List<SsoUserVO> list = list(dto);
        String fileName = "平台用户表模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "平台用户表", SsoUserVO.class, os);
    }

    @Override
    public void resetPassword(Long userId) {
        SsoUser ssoUser = getById(userId);
        CommonResponseEnum.INVALID_ID.assertNull(ssoUser);
        String initPwd = getInitPassword();
        ssoUser.setPasswordHash(getEncoderPwd(initPwd));
        ssoUser.setNeedChangePassword("T");
        saveOrUpdate(ssoUser);
    }

    private static QueryWrapper buildQueryWrapper(SsoUserListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SsoUser.class);
        if (Utils.isNotNull(dto.getUsername())) {
            wrapper.like(SsoUser::getUsername, dto.getUsername());
        }
        if (Utils.isNotNull(dto.getNickname())) {
            wrapper.like(SsoUser::getNickname, dto.getNickname());
        }
        if (Utils.isNotNull(dto.getPhone())) {
            wrapper.like(SsoUser::getPhone, dto.getPhone());
        }
        if (Utils.isNotNull(dto.getEmail())) {
            wrapper.like(SsoUser::getEmail, dto.getEmail());
        }
        if (Utils.isNotNull(dto.getPlatformUserStatusCd())) {
            wrapper.eq(SsoUser::getPlatformUserStatusCd, dto.getPlatformUserStatusCd());
        }
        return wrapper;
    }

    private String getInitPassword() {
        return SysConfigUtils.getConfValue("platform.user.initPwd");
    }

    private static String getEncoderPwd(String pwd) {
        return BCrypt.hashpw(pwd, BCrypt.gensalt(12));
    }

    private static boolean matchEncoderPwd(String pwd, String pwdEncoder) {
        return BCrypt.checkpw(pwd, pwdEncoder);
    }

    public static void main(String[] args) {
        System.out.println("加密密码 ==" + getEncoderPwd("sz@Yanfa2025"));
        System.out.println("匹配密码 ==" + matchEncoderPwd("sz@Yanfa2025", "$2a$12$qjPvtq0pCDLK4wCESNwxKOEfAO7JIPQ4eb5nMiBxnXDUtIXokhBy6"));
    }

}