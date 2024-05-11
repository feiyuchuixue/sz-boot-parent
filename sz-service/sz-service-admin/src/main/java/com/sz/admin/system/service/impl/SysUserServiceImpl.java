package com.sz.admin.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
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
import com.sz.admin.system.service.SysPermissionService;
import com.sz.admin.system.service.SysUserDeptService;
import com.sz.admin.system.service.SysUserService;
import com.sz.core.common.entity.BaseUserInfo;
import com.sz.core.common.entity.LoginUser;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.enums.UserSubjectEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.*;
import com.sz.platform.common.enums.AdminResponseEnum;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import com.sz.redis.CommonKeyConstants;
import com.sz.redis.RedisCache;
import com.sz.redis.RedisService;
import com.sz.redis.RedisUtils;
import com.sz.security.core.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    private final RedisService redisService;

    private final RedisCache redisCache;

    private final SysPermissionService sysPermissionService;

    private final EventPublisher eventPublisher;

    private final SysUserDeptService userDeptService;


    /**
     * 获取认证账户信息接角色信息
     *
     * @param username
     * @return
     */
    @Override
    public SysUserVO getSysUserByUsername(String username) {
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(SysUser::getUsername, username);

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
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(SysUser::getId, userId);
        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        SysUserVO sysUserVO = new SysUserVO();
        BeanCopyUtils.springCopy(one, sysUserVO);
        return sysUserVO;
    }

    /**
     * 用户注册
     *
     * @param dto
     */
    @Override
    public void register(RegisterUserDTO dto) {
        int usernameCount = this.mapper.validUsername(dto.getUsername());
        // 断言: 用户名已存在
        CommonResponseEnum.USERNAME_EXISTS.assertTrue(usernameCount > 0);
        String encodePwd = BCrypt.hashpw(dto.getPwd(), BCrypt.gensalt(10));
        SysUser sysUser = new SysUser();
        sysUser.setUsername(dto.getUsername());
        sysUser.setPwd(encodePwd);
        sysUser.setPhone(dto.getPhone());
        sysUser.setNickname(dto.getNickName());
        save(sysUser);
    }

    /**
     * 后台创建用户
     *
     * @param dto 用户信息
     */
    @Override
    public void create(SysUserAddDTO dto) {
        SysUser user = BeanCopyUtils.springCopy(dto, SysUser.class);
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(SysUser::getUsername, dto.getUsername());
        CommonResponseEnum.USERNAME_EXISTS.assertTrue(count(wrapper) > 0);
        String encodePwd = getEncoderPwd(getInitPassword());
        user.setPwd(encodePwd);
        user.setAccountStatusCd("1000001");
        user.setUserTagCd("1001003");
        save(user);
    }

    /**
     * 更新用户
     *
     * @param dto 用户信息
     */
    @Override
    public void update(SysUserAddDTO dto) {
        SysUser user = BeanCopyUtils.springCopy(dto, SysUser.class);
        // 检查用户是否存在
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(SysUser::getId, dto.getId());
        AdminResponseEnum.INVALID_USER.assertTrue(count(wrapper) < 1);
        wrapper = QueryWrapper.create()
                .eq(SysUser::getUsername, dto.getUsername())
                .ne(SysUser::getId, dto.getId());
        // 检查用户名是否冲突
        CommonResponseEnum.USERNAME_EXISTS.assertTrue(count(wrapper) > 0);
        redisService.clearUserInfo(user.getUsername());
        updateById(user);
    }


    /**
     * 删除用户
     *
     * @param dto 用户id数组
     */
    @Override
    @Transactional
    public void remove(SelectIdsDTO dto) {
        List<Long> ids = (List<Long>) dto.getIds();
        QueryWrapper wrapper = QueryWrapper.create()
                .in(SysUser::getId, dto.getIds());
        // 检查用户是否存在
        AdminResponseEnum.INVALID_ID.assertTrue(count(wrapper) < 1);
        UpdateChain.of(SysUser.class)
                .set(SysUser::getDelFlag, "T")
                .where(SysUser::getId).in(dto.getIds())
                .update();
        // userDeptService.unbind(ids); // 解绑_用户-部门关系
        // 解绑_用户-leader关系
        // 解绑_用户-角色关系
    }

    /**
     * 详情
     *
     * @param id 用户id
     * @return {@link SysUser}
     */
    @Override
    public SysUser detail(Long id) {
        SysUser user = getById(id);
        AdminResponseEnum.INVALID_ID.assertNull(user);
        return user;
    }

    @Override
    public PageResult<SysUserVO> list(SysUserQueryDTO dto) {
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
        List<Long> userIds = userList.stream()
                .map(SysUserVO::getId)
                .collect(Collectors.toList());

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
        List<Long> userIds = userList.stream()
                .map(SysUserVO::getId)
                .collect(Collectors.toList());

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
    public SysUserRoleVO findSysUserRole(Integer userId) {
        List<SysRole> sysRoleList = QueryChain.of(this.sysRoleMapper).list();
        List<SysUserRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(sysRoleList, SysUserRoleVO.RoleInfoVO.class);
        List<SysUserRole> userRoles = QueryChain.of(sysUserRoleMapper)
                .eq(SysUserRole::getUserId, userId)
                .list();
        List<Long> roleIds = new ArrayList<>();
        if (Utils.isNotNull(userRoles)) {
            roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());
        }
        SysUserRoleVO sysUserRoleVO = new SysUserRoleVO();
        sysUserRoleVO.setRoleInfoVOS(roleInfoVOS);
        sysUserRoleVO.setSelectIds(roleIds);
        return sysUserRoleVO;
    }

    @Override
    public void changeSysUserRole(SysUserRoleDTO dto) {
        // 删除当前用户下的所有角色
        UpdateChain.of(sysUserRoleMapper)
                .eq(SysUserRole::getUserId, dto.getUserId())
                .remove();

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
        SysUser sysUser = getById(StpUtil.getLoginIdAsInt());
        return BeanCopyUtils.springCopy(sysUser, SysUserVO.class);
    }

    /**
     * 更改（当前用户）密码
     *
     * @param dto dto
     */
    @Override
    public void changePassword(SysUserPasswordDTO dto) {
        SysUser sysUser = getById(StpUtil.getLoginIdAsInt()); //获取当前用户id
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(matchEncoderPwd(dto.getOldPwd(), sysUser.getPwd()));
        sysUser.setPwd(getEncoderPwd(dto.getNewPwd()));
        updateById(sysUser);
        redisService.clearUserInfo(sysUser.getUsername());
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
            // SaSession saSession = StpUtil.stpLogic.getTokenSessionByToken(token, false); // 根据token获取用户session
            SaSession saSession = StpUtil.getTokenSessionByToken(token);
            // 1. 查询当前用户的最新用户权限信息
            LoginUser loginUser = buildLoginUser(Long.valueOf(userId + ""));
            // 2. 更新redis信息
            saSession.set(LoginUtils.USER_KEY, loginUser);
            log.warn("角色权限变更, 同步更新用户信息, userId:{}, token:{}", userId, token);
        }

    }

    @Override
    public LoginUser buildLoginUser(String username, String password) {
        boolean hasKey = RedisUtils.hasKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        Object value = RedisUtils.getValue(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        long count = hasKey ? Long.parseLong(String.valueOf(value)) : 0;
        CommonResponseEnum.CNT_PASSWORD_ERR.assertTrue(hasKey && (count >= 5));
        SysUserVO userVo = getSysUserByUsername(username);
        Long userId = userVo.getId();
        // 用户状态校验（禁用状态校验）
        validateUserStatus(userVo);
        // 密码校验
        validatePassword(password, userVo.getPwd(), username);
        LoginUser loginUser = getLoginUser(userId, userVo);
        return loginUser;
    }

    @Override
    public LoginUser buildLoginUser(Long userId) {
        SysUserVO userVo = getSysUserByUserId(userId);
        LoginUser loginUser = getLoginUser(userId, userVo);
        return loginUser;
    }

    @Override
    public void unlock(SelectIdsDTO dto) {
        List ids = dto.getIds();
        for (Object id : ids) {
            RedisUtils.removeKey(CommonKeyConstants.SYS_PWD_ERR_CNT, Utils.getStringVal(id));
        }
    }

    @NotNull
    private LoginUser getLoginUser(Long userId, SysUserVO userVo) {
        BaseUserInfo userInfo = BeanCopyUtils.springCopy(userVo, BaseUserInfo.class);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserInfo(userInfo);
        loginUser.setPermissions(sysPermissionService.getMenuPermissions(userId));   // 获取用户permissions
        loginUser.setRoles(sysPermissionService.getRoles(userId)); // 获取用户角色
        return loginUser;
    }

    private void validateUserStatus(SysUserVO user) {
        CommonResponseEnum.BAD_USERNAME_STATUS_INVALID.assertFalse(("1000001").equals(user.getAccountStatusCd()));
    }

    private void validatePassword(String password, String hashedPassword, String username) {
        boolean checkpwed = BCrypt.checkpw(password, hashedPassword);
        if (!checkpwed) redisCache.countPwdErr(username);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(checkpwed);
    }

    @Override
    public void bindUserDept(UserDeptDTO dto) {
        userDeptService.bind(dto);
    }


}