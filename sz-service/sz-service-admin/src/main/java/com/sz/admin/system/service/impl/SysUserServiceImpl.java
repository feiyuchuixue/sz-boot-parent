package com.sz.admin.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysRoleMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.mapper.SysUserRoleMapper;
import com.sz.admin.system.pojo.dto.sysmenu.SysUserRoleDTO;
import com.sz.admin.system.pojo.dto.sysuser.*;
import com.sz.admin.system.pojo.po.SysRole;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.po.SysUserRole;
import com.sz.admin.system.pojo.vo.sysuser.*;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.SysPermissionService;
import com.sz.admin.system.service.SysUserDeptService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.BaseUserInfo;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.*;
import com.sz.mysql.DataScopeProperties;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import com.sz.redis.CommonKeyConstants;
import com.sz.redis.RedisCache;
import com.sz.redis.RedisUtils;
import com.sz.security.core.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    private final RedisCache redisCache;

    private final SysPermissionService sysPermissionService;

    private final EventPublisher eventPublisher;

    private final SysUserDeptService userDeptService;

    private final DataScopeProperties dataScopeProperties;

    private final SysMenuService menuService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 获取认证账户信息接角色信息
     *
     * @param username
     * @return
     */
    @Override
    public SysUserVO getSysUserByUsername(String username) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getUsername, username);

        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        SysUserVO sysUserVO = new SysUserVO();
        BeanCopyUtils.springCopy(one, sysUserVO);
        return sysUserVO;
    }

    /**
     * 获取认证账户信息接角色信息
     *
     * @param userId
     * @return
     */
    @Override
    public SysUserVO getSysUserByUserId(Long userId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getId, userId);
        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        SysUserVO sysUserVO = BeanCopyUtils.copy(one, SysUserVO.class);
        return sysUserVO;
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
        String encodePwd = getEncoderPwd(getInitPassword());
        user.setPwd(encodePwd);
        user.setAccountStatusCd("1000001");
        user.setUserTagCd("1001003");
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
        redisCache.clearUserInfo(one.getUsername());
        updateById(user);
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
        PageResult<SysUserVO> result = null;
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

        // 查询用户的部门信息并转换为 Map
        Map<Long, UserDeptInfoVO> userDeptMap = new HashMap<>();
        List<UserDeptInfoVO> userDeptList = this.mapper.queryUserDeptInfo(userIds);
        if (userDeptList != null) {
            for (UserDeptInfoVO userDeptInfoVO : userDeptList) {
                userDeptMap.put(userDeptInfoVO.getUserId(), userDeptInfoVO);
            }
        }

        // 遍历用户列表，设置用户的部门信息
        for (SysUserVO user : userList) {
            // 检查部门信息是否存在
            if (userDeptMap.containsKey(user.getId())) {
                UserDeptInfoVO infoVO = userDeptMap.get(user.getId());
                user.setDeptInfo(infoVO.getDeptInfos());
                user.setDeptIds(infoVO.getDeptIds());
            }
        }
    }

    private void setUserRoleInfo(List<SysUserVO> userList) {
        if (userList.isEmpty()) {
            return;
        }
        // 获取所有用户的 ID 列表
        List<Long> userIds = userList.stream().map(SysUserVO::getId).collect(Collectors.toList());

        // 查询用户的部门信息并转换为 Map
        Map<Long, UserRoleInfoVO> userRoleMap = new HashMap<>();
        List<UserRoleInfoVO> userDeptList = this.mapper.queryUserRoleInfo(userIds);
        if (userDeptList != null) {
            for (UserRoleInfoVO infoVO : userDeptList) {
                userRoleMap.put(infoVO.getUserId(), infoVO);
            }
        }
        // 遍历用户列表，设置用户的部门信息
        for (SysUserVO user : userList) {
            // 检查部门信息是否存在
            if (userRoleMap.containsKey(user.getId())) {
                UserRoleInfoVO infoVO = userRoleMap.get(user.getId());
                user.setRoleInfo(infoVO.getRoleInfos());
                user.setRoleIds(infoVO.getRoleIds());
            }
        }
    }

    private String getEncoderPwd(String pwd) {
        return BCrypt.hashpw(pwd, BCrypt.gensalt(10));
    }

    private boolean matchEncoderPwd(String pwd, String pwdEncoder) {
        return BCrypt.checkpw(pwd, pwdEncoder);
    }

    @Override
    public SysUserRoleVO findSysUserRole(Long userId) {
        List<SysRole> sysRoleList = QueryChain.of(this.sysRoleMapper).list();
        List<SysUserRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(sysRoleList, SysUserRoleVO.RoleInfoVO.class);
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

    @Override
    public void changeSysUserRole(SysUserRoleDTO dto) {
        // 删除当前用户下的所有角色
        UpdateChain.of(sysUserRoleMapper).eq(SysUserRole::getUserId, dto.getUserId()).remove();

        if (Utils.isNotNull(dto.getRoleIds())) {
            sysUserRoleMapper.insertBatchSysUserRole(dto.getRoleIds(), dto.getUserId());
        }

        List<Long> userIds = new ArrayList<>();
        userIds.add(dto.getUserId());
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    /**
     * 获取用户信息
     *
     * @return {@link SysUserVO}
     */
    @Override
    public SysUserVO getUserInfo() {
        SysUser sysUser = getById(LoginUtils.getLoginUser().getUserInfo().getId());
        return BeanCopyUtils.springCopy(sysUser, SysUserVO.class);
    }

    /**
     * 更改（当前用户）密码
     *
     * @param dto
     *            dto
     */
    @Override
    public void changePassword(SysUserPasswordDTO dto) {
        SysUser sysUser = getById(StpUtil.getLoginIdAsInt()); // 获取当前用户id
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(matchEncoderPwd(dto.getOldPwd(), sysUser.getPwd()));
        sysUser.setPwd(getEncoderPwd(dto.getNewPwd()));
        updateById(sysUser);
        redisCache.clearUserInfo(sysUser.getUsername());
    }

    /**
     * 重置密码
     *
     * @param id
     */
    @Override
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(user);
        user.setPwd(getEncoderPwd(getInitPassword()));
        updateById(user);
    }

    private String getInitPassword() {
        return SysConfigUtils.getConfValue("sys.user.initPwd");
    }

    @Override
    public void syncUserInfo(Object userId) {
        List<String> tokens = StpUtil.getTokenValueListByLoginId(userId);
        if (tokens.isEmpty()) {
            return;
        }
        for (String token : tokens) {
            // SaSession saSession = StpUtil.stpLogic.getTokenSessionByToken(token, false);
            // // 根据token获取用户session
            SaSession saSession = StpUtil.getTokenSessionByToken(token);
            // 1. 查询当前用户的最新用户权限信息
            LoginUser loginUser = buildLoginUser(Long.valueOf(userId + ""));
            // 2. 更新redis信息
            saSession.set(LoginUtils.USER_KEY, loginUser);
            log.warn(" 用户元数据变更, 同步更新用户信息, userId:{}, token:{}", userId, token);
        }
    }

    @Override
    public LoginUser buildLoginUser(String username, String password) {
        boolean hasKey = RedisUtils.hasKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        Object value = RedisUtils.getValue(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        long count = hasKey ? Long.parseLong(String.valueOf(value)) : 0;
        if (!"preview".equals(activeProfile)) { // 预览环境不做账号锁定
            String maxErrCnt = SysConfigUtils.getConfValue("sys.pwd.errCnt");
            CommonResponseEnum.CNT_PASSWORD_ERR.assertTrue(hasKey && (count >= Utils.getIntVal(maxErrCnt)));
        }
        SysUserVO userVo = getSysUserByUsername(username);
        Long userId = userVo.getId();
        // 用户状态校验（禁用状态校验）
        validateUserStatus(userVo);
        // 密码校验
        validatePassword(password, userVo.getPwd(), username);
        LoginUser loginUser = getLoginUser(userVo);
        return loginUser;
    }

    @Override
    public LoginUser buildLoginUser(Long userId) {
        SysUserVO userVo = getSysUserByUserId(userId);
        LoginUser loginUser = getLoginUser(userVo);
        return loginUser;
    }

    @Override
    public void unlock(SelectIdsDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty())
            return;
        String[] ids = dto.getIds().stream().map(Utils::getStringVal).filter(Objects::nonNull).toArray(String[]::new);
        List<SysUserVO> sysUserVOS = this.mapper.queryAllSysUserNameList(ids);
        for (SysUserVO sysUserVO : sysUserVOS) {
            RedisUtils.removeKey(CommonKeyConstants.SYS_PWD_ERR_CNT, Utils.getStringVal(sysUserVO.getUsername()));
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
        loginUser.setDepts(sysPermissionService.getDepts(sysUser)); // 获取用户部门
        loginUser.setDeptAndChildren(sysPermissionService.getDeptAndChildren(sysUser)); // 获取用户部门及子孙节点
        if (!dataScopeProperties.isEnabled())
            return loginUser; // 未开启数据权限控制，结束逻辑return ！！！

        Map<String, String> btmPermissionMap = menuService.getBtnMenuByPermissions(loginUser.getPermissions());
        Set<String> findMenuIds = new HashSet<>();
        for (String menuIds : btmPermissionMap.values()) {
            findMenuIds.add(menuIds);
        }
        loginUser.setPermissionAndMenuIds(btmPermissionMap);
        Map<String, String> ruleMap = sysPermissionService.buildMenuRuleMap(sysUser, findMenuIds);
        String customUserKey = "userRule";
        if (ruleMap.containsKey(customUserKey)) {
            String str = ruleMap.get(customUserKey);
            Map<String, Set<Long>> map = JsonUtils.parseObject(str, new TypeReference<Map<String, Set<Long>>>() {
            });
            ruleMap.remove(customUserKey);
            loginUser.setUserRuleMap(map);
        }
        String customDeptKey = "deptRule";
        if (ruleMap.containsKey(customDeptKey)) {
            String str = ruleMap.get(customDeptKey);
            Map<String, Set<Long>> map = JsonUtils.parseObject(str, new TypeReference<Map<String, Set<Long>>>() {
            });
            ruleMap.remove(customDeptKey);
            loginUser.setDeptRuleMap(map);
        }
        loginUser.setRuleMap(ruleMap); // 获取菜单的查询规则
        return loginUser;
    }

    private void validateUserStatus(SysUserVO user) {
        CommonResponseEnum.BAD_USERNAME_STATUS_INVALID.assertFalse(("1000001").equals(user.getAccountStatusCd()));
    }

    private void validatePassword(String password, String hashedPassword, String username) {
        String timeout = SysConfigUtils.getConfValue("sys_pwd.lockTime");
        boolean checkpwd = BCrypt.checkpw(password, hashedPassword);
        if (!checkpwd)
            redisCache.countPwdErr(username, Utils.getLongVal(timeout).longValue());
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(checkpwd);
    }

    @Override
    public void bindUserDept(UserDeptDTO dto) {
        userDeptService.bind(dto);
        if (Utils.isNotNull(dto.getUserIds())) {
            eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(dto.getUserIds())));
        }
    }

    @Override
    public List<UserOptionVO> getUserOptions() {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.NICKNAME);
        return listAs(wrapper, UserOptionVO.class);
    }

}