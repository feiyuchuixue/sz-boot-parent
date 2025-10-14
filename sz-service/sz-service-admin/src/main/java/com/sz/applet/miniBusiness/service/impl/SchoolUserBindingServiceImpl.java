package com.sz.applet.miniBusiness.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.applet.miniBusiness.mapper.SchoolUserBindingMapper;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingUpdateBO;
import com.sz.applet.miniBusiness.pojo.po.SchoolUserBinding;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserBindingVO;
import com.sz.applet.miniBusiness.service.SchoolUserBindingService;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.utils.MapstructUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sz.applet.miniBusiness.pojo.po.table.SchoolUserBindingTableDef.SCHOOL_USER_BINDING;

/**
 * <p>
 * 学校用户绑定小程序用户 服务实现类
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
@Service
@RequiredArgsConstructor
public class SchoolUserBindingServiceImpl extends ServiceImpl<SchoolUserBindingMapper, SchoolUserBinding> implements SchoolUserBindingService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SchoolUserBindingCreateBO bo) {
        SchoolUserBinding binding = MapstructUtils.convert(bo, SchoolUserBinding.class);
        binding.setStatus(0); // 默认待审核状态
        save(binding);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SchoolUserBindingUpdateBO bo) {
        SchoolUserBinding binding = MapstructUtils.convert(bo, SchoolUserBinding.class);
        updateById(binding);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        removeByIds(dto.getIds());
    }

    @Override
    public SchoolUserBindingVO detail(Long id) {
        SchoolUserBinding binding = getById(id);
        return MapstructUtils.convert(binding, SchoolUserBindingVO.class);
    }

    @Override
    public Object page(SchoolUserBindingListBO bo) {
        // TODO: 实现分页查询
        return null;
    }

    @Override
    public List<SchoolUserBindingVO> list(SchoolUserBindingListBO bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        return listAs(queryWrapper, SchoolUserBindingVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, Integer status, String remark) {
        SchoolUserBinding binding = getById(id);
        if (binding != null) {
            binding.setStatus(status);
            updateById(binding);
            // TODO: 添加审核备注记录
        }
    }

    @Override
    public boolean isBound(Long miniUserId, Long schoolUserId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(SCHOOL_USER_BINDING)
                .where(SCHOOL_USER_BINDING.MINI_USER_ID.eq(miniUserId))
                .and(SCHOOL_USER_BINDING.SCHOOL_USER_ID.eq(schoolUserId))
                .and(SCHOOL_USER_BINDING.STATUS.eq(1)) // 只有审核通过的才算已绑定
                .and(SCHOOL_USER_BINDING.DEL_FLAG.eq("F"));
        return exists(queryWrapper);
    }

    /**
     * 构建查询条件
     *
     * @param bo 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper buildQueryWrapper(SchoolUserBindingListBO bo) {
        return QueryWrapper.create()
                .select()
                .from(SCHOOL_USER_BINDING)
                .where(SCHOOL_USER_BINDING.SCHOOL_USER_ID.eq(bo.getSchoolUserId()))
                .and(SCHOOL_USER_BINDING.MINI_USER_ID.eq(bo.getMiniUserId()))
                .and(SCHOOL_USER_BINDING.STATUS.eq(bo.getStatus()))
                .and(SCHOOL_USER_BINDING.DEL_FLAG.eq("F"))
                .orderBy(SCHOOL_USER_BINDING.CREATE_TIME.desc());
    }
}