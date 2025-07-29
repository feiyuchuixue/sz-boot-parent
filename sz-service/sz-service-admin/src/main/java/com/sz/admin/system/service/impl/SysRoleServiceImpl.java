package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysRoleMapper;
import com.sz.admin.system.mapper.SysRoleMenuMapper;
import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleCreateDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleListDTO;
import com.sz.admin.system.pojo.dto.sysrole.SysRoleUpdateDTO;
import com.sz.admin.system.pojo.po.SysRole;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.common.RoleVO;
import com.sz.admin.system.service.SysRoleService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sz.admin.system.pojo.po.table.SysRoleTableDef.SYS_ROLE;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public void create(SysRoleCreateDTO dto) {
        SysRole sysRole = BeanCopyUtils.copy(dto, SysRole.class);
        long count;
        count = QueryChain.of(SysRole.class).eq(SysRole::getRoleName, dto.getRoleName()).count();
        CommonResponseEnum.EXISTS.message("roleName已存在").assertTrue(count > 0);
        if (Utils.isNotNull(dto.getPermissions())) {
            count = QueryChain.of(SysRole.class).eq(SysRole::getPermissions, dto.getPermissions()).count();
            CommonResponseEnum.EXISTS.message("permissions已存在").assertTrue(count > 0);
        }
        save(sysRole);
    }

    @Override
    public void update(SysRoleUpdateDTO dto) {
        SysRole sysRole = BeanCopyUtils.copy(dto, SysRole.class);
        long count;
        count = QueryChain.of(SysRole.class).eq(SysRole::getRoleName, dto.getRoleName()).ne(SysRole::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("roleName已存在").assertTrue(count > 0);
        if (Utils.isNotNull(dto.getPermissions())) {
            count = QueryChain.of(SysRole.class).eq(SysRole::getPermissions, dto.getPermissions()).ne(SysRole::getId, dto.getId()).count();
            CommonResponseEnum.EXISTS.message("permissions已存在").assertTrue(count > 0);
        }
        updateById(sysRole);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        removeByIds(dto.getIds());
    }

    @Override
    public void removeByMenuId(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().in(SysRoleMenu::getMenuId, dto.getIds());
        sysRoleMenuMapper.deleteByQuery(wrapper);
    }

    @Override
    public PageResult<SysRole> list(SysRoleListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getRoleName())) {
            wrapper.like(SysRole::getRoleName, dto.getRoleName());
        }
        return PageUtils.getPageResult(page(PageUtils.getPage(dto), wrapper));
    }

    @Override
    public PageResult<RoleVO> pageSelector(SelectorQueryDTO dto) {
        String keyword = dto.getKeyword();
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_ROLE.ID, SYS_ROLE.ROLE_NAME.as("name"), SYS_ROLE.PERMISSIONS)
                .orderBy(SYS_ROLE.CREATE_TIME.asc());
        if (keyword != null && !keyword.isEmpty()) {
            QueryCondition condition = SYS_ROLE.ROLE_NAME.like(keyword).or(SYS_ROLE.PERMISSIONS.like(keyword));
            wrapper.and(condition);
        }
        Page<RoleVO> page = pageAs(PageUtils.getPage(dto), wrapper, RoleVO.class);
        return PageUtils.getPageResult(page);
    }

}
