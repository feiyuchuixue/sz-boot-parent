package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.SchoolUserMapper;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserUpdateBO;
import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserVO;
import com.sz.applet.miniBusiness.service.SchoolUserService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.utils.MapstructUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.sz.applet.miniBusiness.pojo.po.table.SchoolUserTableDef.SCHOOL_USER;

/**
 * <p>
 * 学校师生表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Service
@RequiredArgsConstructor
public class SchoolUserServiceImpl extends ServiceImpl<SchoolUserMapper, SchoolUser> implements SchoolUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SchoolUserCreateBO bo) {
        SchoolUser schoolUser = MapstructUtils.convert(bo, SchoolUser.class);
        // 默认设置为待审核状态
        schoolUser.setStatus(0);
        save(schoolUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SchoolUserUpdateBO bo) {
        SchoolUser schoolUser = MapstructUtils.convert(bo, SchoolUser.class);
        CommonResponseEnum.NOT_FOUND.assertNull(schoolUser, "学校师生不存在或已被删除");
        updateById(schoolUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        removeByIds(dto.getIds());
    }

    @Override
    public SchoolUserVO detail(Long id) {
        SchoolUser schoolUser = getById(id);
        CommonResponseEnum.NOT_FOUND.assertNull(schoolUser, "学校师生不存在或已被删除");
        return MapstructUtils.convert(schoolUser, SchoolUserVO.class);
    }

    @Override
    public Page<SchoolUserVO> page(SchoolUserListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        return this.pageAs(PageUtils.getPage(bo), queryWrapper, SchoolUserVO.class);
    }

    @Override
    public List<SchoolUserVO> list(SchoolUserListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        return listAs(queryWrapper, SchoolUserVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, Integer status, String remark) {
        SchoolUser schoolUser = getById(id);
        CommonResponseEnum.NOT_FOUND.assertNull(schoolUser, "学校师生不存在或已被删除");
        
        schoolUser.setStatus(status);
        schoolUser.setReviewerId(/* 当前审核员ID */ null); // TODO: 获取当前登录用户ID
        schoolUser.setReviewTime(new Date());
        schoolUser.setReviewRemark(remark);
        updateById(schoolUser);
    }

    /**
     * 构建查询条件
     *
     * @param bo 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper buildQueryWrapper(SchoolUserListBO bo) {
        return QueryWrapper.create()
                .select()
                .from(SCHOOL_USER)
                .where(SCHOOL_USER.NAME.like(bo.getName()))
                .and(SCHOOL_USER.IDENTITY.eq(bo.getIdentity()))
                .and(SCHOOL_USER.STATUS.eq(bo.getStatus()))
                .and(SCHOOL_USER.DEL_FLAG.eq("F"))
                .orderBy(SCHOOL_USER.CREATE_TIME.desc());
    }
}