package com.sz.admin.system.service.impl;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDeptRoleMapper;
import com.sz.admin.system.mapper.SysRoleMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.admin.system.pojo.dto.sysuser.*;
import com.sz.admin.system.pojo.po.*;
import com.sz.admin.system.pojo.vo.common.UserVO;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.admin.system.pojo.vo.sysuser.*;
import com.sz.admin.system.service.*;
import com.sz.core.common.entity.BaseUserInfo;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.*;
import com.sz.db.DataScopeProperties;
import com.sz.platform.constant.config.AdminConfigKeyConstant;
import com.sz.platform.constant.config.UserConfigKeyConstant;
import com.sz.platform.constant.dict.AccountStatusConstant;
import com.sz.platform.constant.dict.UserTagConstant;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import com.sz.platform.socket.SocketService;
import com.sz.redis.CommonKeyConstants;
import com.sz.redis.RedisCache;
import com.sz.redis.RedisUtils;
import com.sz.security.core.util.LoginUtils;
import com.sz.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.sz.admin.system.pojo.po.table.SysDeptClosureTableDef.SYS_DEPT_CLOSURE;
import static com.sz.admin.system.pojo.po.table.SysDeptTableDef.SYS_DEPT;
import static com.sz.admin.system.pojo.po.table.SysRoleTableDef.SYS_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserDeptTableDef.SYS_USER_DEPT;
import static com.sz.admin.system.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserTableDef.SYS_USER;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2022-10-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysDeptRoleMapper sysDeptRoleMapper;

    private final RedisCache redisCache;

    private final SysPermissionService sysPermissionService;

    private final EventPublisher eventPublisher;

    private final SysUserDeptService userDeptService;

    private final DataScopeProperties dataScopeProperties;

    private final SysMenuService menuService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final AuthService authService;

    private final SysRoleMenuService sysRoleMenuService;

    private final SocketService socketService;

    private final SysUserRoleService sysUserRoleService;

    private final SysDeptClosureService sysDeptClosureService;

    /**
     * 获取认证账户信息接角色信息
     *
     * @param username
     *            用户名
     * @return 用户信息
     */
    @Override
    public SysUserVO getSysUserByUsername(String username) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getUsername, username);

        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        SysUserVO sysUserVO = new SysUserVO();
        BeanCopyUtils.copy(one, sysUserVO);
        return sysUserVO;
    }

    /**
     * 获取认证账户信息接角色信息
     *
     * @param userId
     *            用户id
     * @return 用户信息
     */
    @Override
    public SysUserVO getSysUserByUserId(Long userId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getId, userId);
        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        return BeanCopyUtils.copy(one, SysUserVO.class);
    }

    /**
     * 后台创建用户
     *
     * @param dto
     *            用户信息
     */
    @Transactional
    @Override
    public void create(SysUserCreateDTO dto) {
        SysUser user = BeanCopyUtils.copy(dto, SysUser.class);
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getUsername, dto.getUsername());
        CommonResponseEnum.USERNAME_EXISTS.assertTrue(count(wrapper) > 0);
        String encodePwd = BcryptUtils.hashPwd(getInitPassword());
        user.setPwd(encodePwd);
        user.setAccountStatusCd(AccountStatusConstant.NORMAL);
        user.setUserTagCd(UserTagConstant.NORMAL_USER);
        save(user);

        if (dto.getDeptId() <= 0)
            return;
        UserDeptDTO deptDTO = new UserDeptDTO();
        deptDTO.setDeptIds(Collections.singletonList(dto.getDeptId()));
        deptDTO.setUserIds(Collections.singletonList(user.getId()));
        bindUserDept(deptDTO);
    }

    /**
     * 更新用户
     *
     * @param dto
     *            用户信息
     */
    @Override
    public void update(SysUserUpdateDTO dto) {
        SysUser user = BeanCopyUtils.copy(dto, SysUser.class);
        // 检查用户是否存在
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getId, dto.getId());
        SysUser one = getOne(wrapper);
        CommonResponseEnum.INVALID_USER.assertNull(one);
        updateById(user);
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(Collections.singletonList(user.getId()))));
    }

    /**
     * 删除用户 (逻辑删除，保留数据关系。如部门、权限、角色等)
     *
     * @param dto
     *            用户id数组
     */
    @Override
    @Transactional
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().in(SysUser::getId, dto.getIds());
        // 检查用户是否存在
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) < 1);
        removeByIds(dto.getIds());
    }

    /**
     * 详情
     *
     * @param id
     *            用户id
     * @return {@link SysUser}
     */
    @Override
    public SysUserVO detail(Long id) {
        SysUser user = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(user);
        return BeanCopyUtils.copy(user, SysUserVO.class);
    }

    @Override
    public PageResult<SysUserVO> page(SysUserListDTO dto) {
        PageResult<SysUserVO> result;
        PageUtils.toPage(dto);
        try {
            List<SysUserVO> sysUserVOS;
            if (dto.getDeptId() != null && dto.getDeptId() == -1) { // 查询全部
                sysUserVOS = this.mapper.queryAllSysUserList(dto);
            } else if (dto.getDeptId() != null && dto.getDeptId() == -2) { // 查询未分配部门的列表
                sysUserVOS = this.mapper.querySysUserListNotDept(dto);
            } else { // 查询指定部门的列表
                sysUserVOS = this.mapper.querySysUserListByDept(dto);
            }
            setUserDeptInfo(sysUserVOS);
            setUserRoleInfo(sysUserVOS);
            result = PageUtils.getPageResult(sysUserVOS);
        } finally {
            PageHelper.clearPage();
        }
        return result;
    }

    private void setUserDeptInfo(List<SysUserVO> userList) {
        if (userList.isEmpty()) {
            return;
        }
        // 获取所有用户的 ID 列表
        List<Long> userIds = userList.stream().map(SysUserVO::getId).collect(Collectors.toList());

        // 查询用户的部门明细（不使用 GROUP_CONCAT，兼容 MySQL 和 PostgreSQL）
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER_DEPT.USER_ID, SYS_USER_DEPT.DEPT_ID).from(SYS_USER_DEPT).join(SYS_DEPT)
                .on(SYS_USER_DEPT.DEPT_ID.eq(SYS_DEPT.ID)).where(SYS_USER_DEPT.USER_ID.in(userIds));
        List<UserDeptInfoVO> userDeptList = listAs(wrapper, UserDeptInfoVO.class);

        // Java 层按 userId 分组，将多个 deptId 合并为逗号分隔字符串
        Map<Long, String> userDeptMap = new HashMap<>();
        if (userDeptList != null) {
            userDeptList.stream()
                    .collect(
                            Collectors.groupingBy(UserDeptInfoVO::getUserId, Collectors.mapping(vo -> String.valueOf(vo.getDeptId()), Collectors.joining(","))))
                    .forEach(userDeptMap::put);
        }
        // 遍历用户列表，设置用户的部门信息
        for (SysUserVO user : userList) {
            if (userDeptMap.containsKey(user.getId())) {
                user.setDeptIds(userDeptMap.get(user.getId()));
            }
        }
    }

    private void setUserRoleInfo(List<SysUserVO> userList) {
        if (userList.isEmpty()) {
            return;
        }
        // 获取所有用户的 ID 列表
        List<Long> userIds = userList.stream().map(SysUserVO::getId).collect(Collectors.toList());

        // 查询用户的角色明细（不使用 GROUP_CONCAT，兼容 MySQL 和 PostgreSQL）
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER_ROLE.USER_ID, SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE).innerJoin(SYS_ROLE)
                .on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ID)).where(SYS_USER_ROLE.USER_ID.in(userIds));
        List<UserRoleInfoVO> userRoleList = listAs(wrapper, UserRoleInfoVO.class);

        // Java 层按 userId 分组，将多个 roleId 合并为逗号分隔字符串
        Map<Long, String> userRoleMap = new HashMap<>();
        if (userRoleList != null) {
            userRoleList.stream()
                    .collect(
                            Collectors.groupingBy(UserRoleInfoVO::getUserId, Collectors.mapping(vo -> String.valueOf(vo.getRoleId()), Collectors.joining(","))))
                    .forEach(userRoleMap::put);
        }
        // 遍历用户列表，设置用户的角色信息
        for (SysUserVO user : userList) {
            if (userRoleMap.containsKey(user.getId())) {
                user.setRoleIds(userRoleMap.get(user.getId()));
            }
        }
    }

    @Override
    public SysUserRoleVO findSysUserRole(Long userId) {
        List<SysRole> sysRoleList = QueryChain.of(this.sysRoleMapper).list();
        List<SysUserRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(sysRoleList, SysUserRoleVO.RoleInfoVO.class);
        String superAdminRoleId = SysConfigUtils.getConfValue(AdminConfigKeyConstant.SUPER_ADMIN_ROLE_ID);
        for (SysUserRoleVO.RoleInfoVO roleInfoVO : roleInfoVOS) {
            if (superAdminRoleId.equals(Utils.getStringVal(roleInfoVO.getId()))) {
                roleInfoVO.setDisabled(true);
            }
        }

        List<SysUserRole> userRoles = QueryChain.of(sysUserRoleMapper).eq(SysUserRole::getUserId, userId).list();
        List<Long> roleIds = new ArrayList<>();
        if (Utils.isNotNull(userRoles)) {
            roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        }
        SysUserRoleVO sysUserRoleVO = new SysUserRoleVO();
        sysUserRoleVO.setRoleInfoVOS(roleInfoVOS);
        sysUserRoleVO.setSelectIds(roleIds);
        return sysUserRoleVO;
    }

    @Transactional
    @Override
    public void changeSysUserRole(SysUserRoleDTO dto) {
        // 删除当前用户下的所有角色
        UpdateChain.of(sysUserRoleMapper).eq(SysUserRole::getUserId, dto.getUserId()).remove();

        if (Utils.isNotNull(dto.getRoleIds())) {
            List<SysUserRole> userRoles = dto.getRoleIds().stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(dto.getUserId());
                ur.setRoleId(roleId);
                return ur;
            }).toList();
            sysUserRoleMapper.insertBatch(userRoles);
        }

        List<Long> userIds = new ArrayList<>();
        userIds.add(dto.getUserId());
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    /**
     * 更改（当前用户）密码
     *
     * @param dto
     *            dto
     */
    @Override
    public void changePassword(SysUserPasswordDTO dto) {
        SysUser sysUser = getById(StpUtil.getLoginIdAsLong()); // 获取当前用户id
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(BcryptUtils.matchEncoderPwd(dto.getOldPwd(), sysUser.getPwd()));
        sysUser.setPwd(BcryptUtils.hashPwd(dto.getNewPwd()));
        updateById(sysUser);
        redisCache.clearUserInfo(sysUser.getUsername());
        authService.kickOut(StpUtil.getLoginIdAsLong());
    }

    /**
     * 重置密码
     *
     * @param id
     *            id
     */
    @Override
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(user);
        user.setPwd(BcryptUtils.hashPwd(getInitPassword()));
        updateById(user);
        authService.kickOut(id);
    }

    private String getInitPassword() {
        return SysConfigUtils.getConfValue(UserConfigKeyConstant.INIT_PWD);
    }

    @Override
    public void syncUserInfoWithLoginUser(Long userId, LoginUser loginUser) {
        List<String> tokens = StpUtil.getTokenValueListByLoginId(userId);
        if (tokens.isEmpty()) {
            return;
        }
        int successCount = 0;
        for (String token : tokens) {
            try {
                SaSession saSession = StpUtil.getTokenSessionByToken(token);
                saSession.set(LoginUtils.USER_KEY, loginUser);
                successCount++;
            } catch (SaTokenException e) {
                log.warn("token:{} 已失效, 无需同步用户信息", token);
            }
        }
        log.info("用户元数据变更，同步更新用户信息 userId:{}, 成功:{} / {}", userId, successCount, tokens.size());
        if (successCount > 0) {
            socketService.syncPermission(userId);
        }
    }

    @Override
    public LoginUser buildLoginUser(String username, String password) {
        boolean hasKey = RedisUtils.hasKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        Object value = RedisUtils.getValue(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        long count = hasKey ? Long.parseLong(String.valueOf(value)) : 0;
        if (!"preview".equals(activeProfile)) { // 预览环境不做账号锁定
            String maxErrCnt = SysConfigUtils.getConfValue(UserConfigKeyConstant.PWD_ERR_CNT);
            CommonResponseEnum.CNT_PASSWORD_ERR.assertTrue(hasKey && (count >= Utils.getIntVal(maxErrCnt)));
        }
        SysUserVO userVo = getSysUserByUsername(username);
        // 用户状态校验（禁用状态校验）
        validateUserStatus(userVo);
        // 密码校验
        validatePassword(password, userVo.getPwd(), username);
        return getLoginUser(userVo);
    }

    @Override
    public LoginUser buildLoginUser(Long userId) {
        SysUserVO userVo = getSysUserByUserId(userId);
        return getLoginUser(userVo);
    }

    @Override
    public Map<Long, LoginUser> buildLoginUserBatch(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 批量查用户基本信息
        List<SysUser> users = QueryChain.of(SysUser.class).where(SysUser::getId).in(userIds).list();

        // 2. 分离超管 / 普通用户（超管走单个路径，数据逻辑不同）
        List<SysUser> superAdmins = users.stream().filter(this::isSuperAdminUser).toList();
        List<SysUser> normalUsers = users.stream().filter(u -> !isSuperAdminUser(u)).toList();

        Map<Long, LoginUser> result = new HashMap<>();

        // 3. 超管：走现有单个 buildLoginUser 逻辑，保证行为完全一致
        for (SysUser admin : superAdmins) {
            result.put(admin.getId(), buildLoginUser(admin.getId()));
        }

        if (normalUsers.isEmpty()) {
            return result;
        }

        List<Long> normalUserIds = normalUsers.stream().map(SysUser::getId).toList();

        // 4. 批量查角色：sys_user_role WHERE user_id IN (...) → 1 次查询
        Map<Long, List<String>> userRolesMap = sysUserRoleService.getUserRolesByUserIds(normalUserIds);

        // 5. 批量查直属部门：sys_user_dept WHERE user_id IN (...) → 1 次查询
        List<SysUserDept> userDeptList = QueryChain.of(SysUserDept.class).select(SYS_USER_DEPT.USER_ID, SYS_USER_DEPT.DEPT_ID)
                .where(SYS_USER_DEPT.USER_ID.in(normalUserIds)).list();
        Map<Long, List<Long>> userDeptsMap = userDeptList.stream()
                .collect(Collectors.groupingBy(SysUserDept::getUserId, Collectors.mapping(SysUserDept::getDeptId, Collectors.toList())));

        // 6. 合并所有直属部门ID，批量查子孙节点：sys_dept_closure WHERE ancestor_id IN (...) → 1 次查询
        List<Long> allDeptIds = userDeptList.stream().map(SysUserDept::getDeptId).distinct().toList();
        Map<Long, List<Long>> deptDescendantsMap = sysDeptClosureService.descendantsGroupByAncestor(allDeptIds);

        // 7. 按用户组装 deptAndChildren（将该用户所有直属部门的子孙节点合并去重）
        Map<Long, List<Long>> userDeptAndChildrenMap = new HashMap<>();
        for (Long uid : normalUserIds) {
            List<Long> depts = userDeptsMap.getOrDefault(uid, Collections.emptyList());
            Set<Long> deptAndChildren = new HashSet<>();
            for (Long deptId : depts) {
                deptAndChildren.addAll(deptDescendantsMap.getOrDefault(deptId, Collections.emptyList()));
            }
            userDeptAndChildrenMap.put(uid, new ArrayList<>(deptAndChildren));
        }

        // 8. 按 userId 逐个组装 LoginUser（DB 密集查询均已在上方批量完成）
        // 数据权限范围必须按"用户各自角色集合"独立计算：getUserScope 的合并逻辑
        // （1006005 自定义优先 / 取最小 dataScopeCd）依赖入参角色子集，不能跨用户共享，
        // 否则会导致用户 A 拿到用户 B 的自定义范围。
        for (SysUser user : normalUsers) {
            Long uid = user.getId();
            BaseUserInfo userInfo = BeanCopyUtils.copy(user, BaseUserInfo.class);

            LoginUser loginUser = new LoginUser();
            loginUser.setUserInfo(userInfo);

            // permissions 仍需按 userId 单独查（三表联查，本次不批量化）
            loginUser.setPermissions(sysPermissionService.getMenuPermissions(user));

            List<String> roles = userRolesMap.getOrDefault(uid, Collections.emptyList());
            loginUser.setRoles(new HashSet<>(roles));

            loginUser.setDepts(userDeptsMap.getOrDefault(uid, Collections.emptyList()));
            loginUser.setDeptAndChildren(userDeptAndChildrenMap.getOrDefault(uid, Collections.emptyList()));

            if (dataScopeProperties.isEnabled()) {
                // 按该用户自身的角色集合独立计算数据权限，避免跨用户污染
                Map<Long, RoleMenuScopeVO> userScope = sysRoleMenuService.getUserScope(new HashSet<>(roles));
                loginUser.setDataScope(userScope);
                // getBtnMenuByPermissions 入参是 permissions，按用户自己的 permissions 查（本次不批量化）
                loginUser.setPermissionAndMenuIds(menuService.getBtnMenuByPermissions(loginUser.getPermissions()));
            }

            result.put(uid, loginUser);
        }

        return result;
    }

    /**
     * 判断是否为超级管理员（userTagCd = UserTagConstant.SUPER_ADMIN）
     */
    private boolean isSuperAdminUser(SysUser sysUser) {
        return sysUser != null && UserTagConstant.SUPER_ADMIN.equals(sysUser.getUserTagCd());
    }

    @Override
    public void unlock(SelectIdsDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty())
            return;
        List<String> usernames = QueryChain.of(SysUser.class).select(SYS_USER.USERNAME).where(SYS_USER.DEL_FLAG.eq("F")).and(SYS_USER.ID.in(dto.getIds()))
                .listAs(String.class);
        for (String username : usernames) {
            RedisUtils.removeKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        }
    }

    private LoginUser getLoginUser(SysUserVO userVo) {
        BaseUserInfo userInfo = BeanCopyUtils.copy(userVo, BaseUserInfo.class);
        SysUser sysUser = BeanCopyUtils.copy(userVo, SysUser.class);
        CommonResponseEnum.INVALID_USER.assertNull(sysUser);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserInfo(userInfo);
        loginUser.setPermissions(sysPermissionService.getMenuPermissions(sysUser)); // 获取用户permissions
        loginUser.setRoles(sysPermissionService.getRoles(sysUser)); // 获取用户角色
        // 先查直属部门，再传入 getDeptAndChildren，避免重复查询 sys_user_dept
        List<Long> depts = sysPermissionService.getDepts(sysUser);
        loginUser.setDepts(depts);
        loginUser.setDeptAndChildren(sysPermissionService.getDeptAndChildren(sysUser, depts)); // 获取用户部门及子孙节点
        if (!dataScopeProperties.isEnabled())
            return loginUser; // 未开启数据权限控制，结束逻辑return ！！！

        // 根据角色 获取用户数据权限范围
        Set<String> roles = loginUser.getRoles();
        Map<Long, RoleMenuScopeVO> userScope = sysRoleMenuService.getUserScope(roles);
        loginUser.setDataScope(userScope);

        Map<String, Long> btmPermissionMap = menuService.getBtnMenuByPermissions(loginUser.getPermissions());
        loginUser.setPermissionAndMenuIds(btmPermissionMap);

        return loginUser;
    }

    private void validateUserStatus(SysUserVO user) {
        CommonResponseEnum.BAD_USERNAME_STATUS_INVALID.assertFalse(AccountStatusConstant.NORMAL.equals(user.getAccountStatusCd()));
    }

    private void validatePassword(String password, String hashedPassword, String username) {
        String timeout = SysConfigUtils.getConfValue(UserConfigKeyConstant.PWD_LOCK_TIME);
        boolean checkpwd = BcryptUtils.matchEncoderPwd(password, hashedPassword);
        if (!checkpwd)
            redisCache.countPwdErr(username, Utils.getLongVal(timeout));
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(checkpwd);
    }

    @Override
    public void bindUserDept(UserDeptDTO dto) {
        userDeptService.bind(dto);
        if (Utils.isNotNull(dto.getUserIds()) && Utils.isNotNull(dto.getDeptIds())) {
            bindUserRoleByDept(dto.getUserIds(), dto.getDeptIds());
        }
        if (Utils.isNotNull(dto.getUserIds())) {
            eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(dto.getUserIds())));
        }
    }

    /**
     * 根据部门绑定角色
     */
    private void bindUserRoleByDept(List<Long> userIds, List<Long> deptIds) {
        if (!Utils.isNotNull(userIds) || !Utils.isNotNull(deptIds)) {
            return;
        }

        // 获取用户当前拥有的所有角色
        List<SysUserRole> existingUserRoles = QueryChain.of(sysUserRoleMapper).in(SysUserRole::getUserId, userIds).list();
        Map<Long, Set<Long>> userToRoleIdsMap = existingUserRoles.stream()
                .collect(Collectors.groupingBy(SysUserRole::getUserId, Collectors.mapping(SysUserRole::getRoleId, Collectors.toSet())));
        // 获取部门关联的所有角色
        List<SysDeptRole> deptRoles = QueryChain.of(sysDeptRoleMapper).in(SysDeptRole::getDeptId, deptIds).list();
        Set<Long> deptRoleIds = deptRoles.stream().map(SysDeptRole::getRoleId).collect(Collectors.toSet());
        if (deptRoleIds.isEmpty()) {
            return;
        }
        // 为每个用户构造需要绑定的角色
        List<SysUserRole> toInsert = new ArrayList<>();
        for (Long userId : userIds) {
            Set<Long> userRoleIds = userToRoleIdsMap.getOrDefault(userId, Collections.emptySet());
            deptRoleIds.stream().filter(roleId -> !userRoleIds.contains(roleId)).forEach(roleId -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(userId);
                toInsert.add(sysUserRole);
            });
        }
        if (!toInsert.isEmpty()) {
            sysUserRoleMapper.insertBatch(toInsert);
            // 更新用户元数据
            List<Long> changedUserIds = toInsert.stream().map(SysUserRole::getUserId).distinct().toList();
            eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(changedUserIds)));
        }
    }

    @Override
    public List<UserOptionVO> getUserOptions() {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.NICKNAME);
        return listAs(wrapper, UserOptionVO.class);
    }

    @Override
    public PageResult<UserVO> pageSelector(SelectorQueryDTO dto) {
        String keyword = dto.getKeyword();
        Long parentId = dto.getParentId();

        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.NICKNAME.as("name"), SYS_USER.PHONE).from(SYS_USER);

        if (parentId != null && parentId != -1L) {
            wrapper.join(SYS_USER_DEPT).on(SYS_USER.ID.eq(SYS_USER_DEPT.USER_ID)).join(SYS_DEPT_CLOSURE)
                    .on(SYS_USER_DEPT.DEPT_ID.eq(SYS_DEPT_CLOSURE.DESCENDANT_ID)).join(SYS_DEPT).on(SYS_USER_DEPT.DEPT_ID.eq(SYS_DEPT.ID))
                    .where(SYS_DEPT_CLOSURE.DESCENDANT_ID.isNotNull()).and(SYS_DEPT_CLOSURE.ANCESTOR_ID.eq(parentId)).groupBy(SYS_USER.ID)
                    .orderBy(SYS_USER.CREATE_TIME.asc());
        }

        // 关键词条件
        if (keyword != null && !keyword.isEmpty()) {
            QueryCondition condition = SYS_USER.USERNAME.like(keyword).or(SYS_USER.NICKNAME.like(keyword)).or(SYS_USER.PHONE.like(keyword));
            wrapper.and(condition);
        }

        Page<UserVO> page = pageAs(PageUtils.getPage(dto), wrapper, UserVO.class);
        return PageUtils.getPageResult(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeUserTag(SysUserTagDTO dto) {
        String userTagCd = dto.getUserTagCd();
        List<Long> userIds = dto.getUserIds();
        String superAdminRoleId = SysConfigUtils.getConfValue(AdminConfigKeyConstant.SUPER_ADMIN_ROLE_ID);
        if (userIds.isEmpty()) {
            return;
        }
        UpdateChain.of(SysUser.class).set(SYS_USER.USER_TAG_CD, userTagCd).where(SYS_USER.ID.in(userIds)).update(); // 更新用户标签
        UpdateChain.of(SysUserRole.class).where(SYS_USER_ROLE.USER_ID.in(userIds)).where(SYS_USER_ROLE.ROLE_ID.eq(Utils.getLongVal(superAdminRoleId))).remove(); // 删除超管角色
        List<SysUserRole> userRoles = new ArrayList<>();
        SysUserRole userRole;
        if (UserTagConstant.SUPER_ADMIN.equals(userTagCd)) { // 超管角色用户
            for (Long userId : userIds) {
                userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Utils.getLongVal(superAdminRoleId));
                userRoles.add(userRole);
            }
        }
        if (!userRoles.isEmpty()) {
            sysUserRoleMapper.insertBatch(userRoles);
        }
        // 发布用户权限变更事件
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    @Override
    public UserProfileVO getProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser sysUser = getById(userId);
        UserProfileVO profileVO = BeanCopyUtils.copy(sysUser, UserProfileVO.class);
        profileVO.setAvatar(sysUser.getLogo());
        return profileVO;
    }

    @Override
    public void updateProfile(UserProfileUpdateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = BeanCopyUtils.copy(dto, SysUser.class);
        user.setId(userId);
        user.setLogo(dto.getAvatar());
        updateById(user);
    }

    @Override
    public void updateContact(SysUserContactUpdateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser sysUser = getById(userId);
        // 验证当前密码
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(BcryptUtils.matchEncoderPwd(dto.getPassword(), sysUser.getPwd()));
        // 唯一性校验：不能与其他用户的手机号/邮箱重复
        QueryWrapper uniqueWrapper;
        if ("phone".equals(dto.getField())) {
            uniqueWrapper = QueryWrapper.create().eq(SysUser::getPhone, dto.getValue()).ne(SysUser::getId, userId);
            CommonResponseEnum.EXISTS.message("该手机号已被其他账户使用").assertTrue(count(uniqueWrapper) > 0);
            sysUser.setPhone(dto.getValue());
        } else {
            uniqueWrapper = QueryWrapper.create().eq(SysUser::getEmail, dto.getValue()).ne(SysUser::getId, userId);
            CommonResponseEnum.EXISTS.message("该邮箱已被其他账户使用").assertTrue(count(uniqueWrapper) > 0);
            sysUser.setEmail(dto.getValue());
        }
        updateById(sysUser);
    }

    @Override
    public void unbindContact(SysUserContactUnbindDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser sysUser = getById(userId);
        // 验证当前密码
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(BcryptUtils.matchEncoderPwd(dto.getPassword(), sysUser.getPwd()));
        // 将对应字段置空
        if ("phone".equals(dto.getField())) {
            sysUser.setPhone("");
        } else {
            sysUser.setEmail("");
        }
        updateById(sysUser);
    }

    public static void main(String[] args) {
        System.out.printf("pwd =" + BcryptUtils.hashPwd("sz123456"));
    }
}