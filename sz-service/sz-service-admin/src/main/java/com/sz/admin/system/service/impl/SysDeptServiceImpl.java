package com.sz.admin.system.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDeptLeaderMapper;
import com.sz.admin.system.mapper.SysDeptMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.pojo.dto.common.SelectorQueryDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptCreateDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptListDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptRoleDTO;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptUpdateDTO;
import com.sz.admin.system.pojo.po.SysDept;
import com.sz.admin.system.pojo.po.SysDeptLeader;
import com.sz.admin.system.pojo.po.SysUser;
import com.sz.admin.system.pojo.vo.common.DepartmentVO;
import com.sz.admin.system.pojo.vo.sysdept.*;
import com.sz.admin.system.service.SysDeptClosureService;
import com.sz.admin.system.service.SysDeptLeaderService;
import com.sz.admin.system.service.SysDeptService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.common.event.EventPublisher;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.TreeUtils;
import com.sz.core.util.Utils;
import com.sz.platform.event.PermissionChangeEvent;
import com.sz.platform.event.PermissionMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.sz.admin.system.pojo.po.table.SysDeptLeaderTableDef.SYS_DEPT_LEADER;
import static com.sz.admin.system.pojo.po.table.SysDeptRoleTableDef.SYS_DEPT_ROLE;
import static com.sz.admin.system.pojo.po.table.SysDeptTableDef.SYS_DEPT;
import static com.sz.admin.system.pojo.po.table.SysRoleTableDef.SYS_ROLE;
import static com.sz.admin.system.pojo.po.table.SysUserDeptTableDef.SYS_USER_DEPT;
import static com.sz.admin.system.pojo.po.table.SysUserTableDef.SYS_USER;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-03-20
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysUserMapper userMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysDeptRoleMapper sysDeptRoleMapper;

    private final SysUserDeptMapper sysUserDeptMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysDeptLeaderMapper leaderMapper;

    private final SysDeptLeaderService deptLeaderService;

    private final SysDeptClosureService deptClosureService;

    private final EventPublisher eventPublisher;


    @Transactional
    @Override
    public void create(SysDeptCreateDTO dto) {
        SysDept sysDept = BeanCopyUtils.copy(dto, SysDept.class);
        if (dto.getPid() == 0) {
            sysDept.setDeep(1);
        } else {
            QueryWrapper wrapper = QueryWrapper.create().eq(SysDept::getId, dto.getPid());
            CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
            SysDept parentDept = getOne(wrapper);
            parentDept.setHasChildren("T");
            saveOrUpdate(parentDept);
            sysDept.setDeep(parentDept.getDeep() + 1);
        }
        save(sysDept);
        Long deptId = sysDept.getId();
        Long pid = dto.getPid();
        // 设置负责人
        deptLeaderService.syncLeader(deptId, dto.getLeaders());
        deptClosureService.create(deptId, pid);

    }

    @Transactional
    @Override
    public void update(SysDeptUpdateDTO dto) {
        SysDept sysDept = BeanCopyUtils.copy(dto, SysDept.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysDept::getId, dto.getId());
        SysDept one = getOne(wrapper);
        Long oldPid = one.getPid();
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        if (dto.getPid() == 0) {
            sysDept.setDeep(1);
        } else {
            wrapper = QueryWrapper.create().eq(SysDept::getId, dto.getPid());
            SysDept parent = getOne(wrapper);
            one.setHasChildren("T");
            saveOrUpdate(one);
            sysDept.setDeep(parent.getDeep() + 1);
        }
        // 设置负责人
        deptLeaderService.syncLeader(dto.getId(), dto.getLeaders());
        saveOrUpdate(sysDept);
        // 移动树
        if (!Objects.equals(oldPid, dto.getPid())) {
            deptClosureService.move(dto.getId(), dto.getPid());
        }
    }

    @Override
    public PageResult<SysDeptVO> page(SysDeptListDTO dto) {
        Page<SysDeptVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysDeptVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysDeptVO> list(SysDeptListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DEPT.ALL_COLUMNS, QueryMethods.groupConcat(
                if_(SYS_USER.DEL_FLAG.eq("F"), QueryMethods.concatWs(QueryMethods.string(":"), SYS_DEPT_LEADER.LEADER_ID, SYS_USER.NICKNAME), null_())

        ).as("leader_info")).from(SYS_DEPT).leftJoin(SYS_DEPT_LEADER).on(SYS_DEPT.ID.eq(SYS_DEPT_LEADER.DEPT_ID)).leftJoin(SYS_USER)
                .on(SYS_DEPT_LEADER.LEADER_ID.eq(SYS_USER.ID)).groupBy(SYS_DEPT.ID);
        List<SysDeptVO> deptVOS = listAs(wrapper, SysDeptVO.class);
        setDeptRoleInfo(deptVOS);
        SysDeptVO root = TreeUtils.getRoot(SysDeptVO.class);
        List<SysDeptVO> trees = TreeUtils.buildTree(deptVOS, root);
        return trees.getFirst().getChildren();
    }

    /**
     * 添加部门关联角色信息
     */
    private void setDeptRoleInfo(List<SysDeptVO> deptList) {
        if (deptList.isEmpty()) {
            return;
        }
        // 获取所有部门的 ID 列表，查询角色信息
        List<Long> deptIds = deptList.stream().map(SysDeptVO::getId).collect(Collectors.toList());
        List<DeptRoleInfoVO> deptRoleInfoVOS = QueryChain.of(this.mapper).select(SYS_DEPT.ID.as("deptId"), SYS_ROLE.ID.as("roleId"), SYS_ROLE.ROLE_NAME.as("roleName"))
                .from(SYS_DEPT)
                .leftJoin(SYS_DEPT_ROLE).on(SYS_DEPT.ID.eq(SYS_DEPT_ROLE.DEPT_ID))
                .leftJoin(SYS_ROLE).on(SYS_DEPT_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .and(SYS_DEPT.ID.in(deptIds)).and(SYS_ROLE.ID.isNotNull()).listAs(DeptRoleInfoVO.class);
        Map<Long, List<DeptRoleInfoVO>> roleInfoMap = deptRoleInfoVOS.stream().collect(Collectors.groupingBy(DeptRoleInfoVO::getDeptId));

        // 遍历部门列表，设置部门的角色信息
        for (SysDeptVO dept : deptList) {
            if (roleInfoMap.containsKey(dept.getId())) {
                List<DeptRoleInfoVO> deptRoleInfos = roleInfoMap.get(dept.getId());
                dept.setRoleIds(deptRoleInfos.stream().map(DeptRoleInfoVO::getRoleId).collect(Collectors.joining(",")));
                dept.setRoleInfo(deptRoleInfos.stream().map(item -> item.getRoleId() + ":" + item.getRoleName()).collect(Collectors.joining(",")));
            }
        }
    }


    @Override
    public void remove(SelectIdsDTO dto) {
        List<Long> ids = (List<Long>) dto.getIds();
        CommonResponseEnum.INVALID_ID.assertTrue(ids.isEmpty());
        // 根据要删除的id，获取所有子集id
        List<Long> descendants = deptClosureService.descendants(ids);
        removeByIds(descendants);
    }

    @Override
    public SysDeptVO detail(Object id) {
        SysDept sysDept = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysDept);
        SysDeptVO deptVO = BeanCopyUtils.copy(sysDept, SysDeptVO.class);
        // 查询指定部门的负责人
        List<Long> leaderIds = QueryChain.of(leaderMapper).select(SYS_DEPT_LEADER.LEADER_ID).eq(SysDeptLeader::getDeptId, id).listAs(Long.class);
        deptVO.setLeaders(leaderIds);
        return deptVO;
    }

    @Override
    public List<DeptTreeVO> getDepartmentTreeWithAdditionalNodes() {
        // 获取部门树
        List<DeptTreeVO> trees = getDeptTree(null, false, true);
        // 获取所有用户数量
        long allUserCount = count(QueryWrapper.create().from(SysUser.class));
        // 获取未设置部门的用户数量
        long unsetDeptCount = userMapper.countSysUserListNotDept();
        // 创建全部节点
        DeptTreeVO all = createAllNode(allUserCount);
        // 创建未设置部门节点
        DeptTreeVO unset = createUnsetNode(unsetDeptCount);
        // 将‘全部’、‘未设置部门’节点添加到集合头部
        trees.add(0, all);
        trees.add(1, unset);
        return trees;
    }

    private DeptTreeVO createUnsetNode(Long unsetDeptCount) {
        DeptTreeVO unset = new DeptTreeVO();
        unset.setId(-2L);
        unset.setName("未设置部门");
        AtomicReference<Long> total = new AtomicReference<>(0L);
        LogicDeleteManager.execWithoutLogicDelete(() -> total.set(unsetDeptCount));
        unset.setUserTotal(total.get());
        return unset;
    }

    private DeptTreeVO createAllNode(Long userCount) {
        DeptTreeVO all = new DeptTreeVO();
        all.setId(-1L);
        all.setName("全部");
        all.setUserTotal(userCount);
        return all;
    }

    @Override
    public List<DeptTreeVO> getDeptTree(Integer excludeNodeId, Boolean appendRoot, Boolean needSetTotal) {
        QueryWrapper wrapper = QueryWrapper.create()
                // .orderBy(SysDept::getDeep).asc()
                .orderBy(SysDept::getSort).asc();
        // 获取所有部门信息
        List<SysDept> list = list(wrapper);
        List<DeptTreeVO> deptTreeVOS = BeanCopyUtils.copyList(list, DeptTreeVO.class);
        if (needSetTotal != null && needSetTotal) {
            setUseTotal(deptTreeVOS);
        }
        DeptTreeVO root = TreeUtils.getRoot(DeptTreeVO.class);
        assert root != null;
        root.setName("根部门");
        List<DeptTreeVO> trees = TreeUtils.buildTree(deptTreeVOS, root, excludeNodeId);
        if (appendRoot != null && !appendRoot) {
            if (trees.getFirst().getChildren() == null) {
                trees = new ArrayList<>();
            } else {
                trees = trees.getFirst().getChildren();
            }
        }
        return trees;
    }

    private void setUseTotal(List<DeptTreeVO> deptTreeVOS) {
        // 查询直属部门的数量
        QueryWrapper wrapper = QueryWrapper.create()
                .select(SYS_DEPT.ID, SYS_DEPT.NAME, QueryMethods.count(case_().when(SYS_USER.DEL_FLAG.eq("F")).then(SYS_USER_DEPT.USER_ID).end()).as("total"))
                .from(SYS_DEPT).leftJoin(SYS_USER_DEPT).on(SYS_DEPT.ID.eq(SYS_USER_DEPT.DEPT_ID)).leftJoin(SYS_USER).on(SYS_USER_DEPT.USER_ID.eq(SYS_USER.ID))
                .groupBy(SYS_DEPT.ID, SYS_DEPT.NAME).orderBy(SYS_DEPT.DEEP.asc()).orderBy(SYS_DEPT.SORT.asc());
        List<TotalDeptVO> totalDeptVOS = listAs(wrapper, TotalDeptVO.class);

        /*
         * 查询非直属部门的数量。
         *
         * SQL 查询语句： SELECT d.id, d.name, COUNT(ud.user_id) AS total FROM sys_dept d
         * LEFT JOIN sys_dept_closure c ON d.id = c.ancestor_id LEFT JOIN sys_user_dept
         * ud ON c.descendant_id = ud.dept_id GROUP BY d.id, d.name;
         *
         * 该查询通过 `LEFT JOIN` 连接 `sys_dept`、`sys_dept_closure` 和 `sys_user_dept` 三个表，
         * 统计每个部门下非直属的用户数量，并按部门 ID 和名称分组。
         */
        Map<Long, Long> totalDeptMap = new HashMap<>();
        if (totalDeptVOS != null) {
            totalDeptMap = totalDeptVOS.stream().collect(Collectors.toMap(TotalDeptVO::getId, TotalDeptVO::getTotal));
        }
        for (DeptTreeVO treeVO : deptTreeVOS) {
            if (totalDeptMap.containsKey(treeVO.getId())) {
                treeVO.setUserTotal(totalDeptMap.get(treeVO.getId()));
            }
        }
    }

    /**
     * 查询部门leader信息-穿梭框
     *
     * @return SysDeptLeaderVO
     */
    @Override
    public SysDeptLeaderVO findSysUserDeptLeader() {
        // 查询所有的用户
        List<SysDeptLeaderVO.LeaderInfoVO> userInfos = QueryChain.of(userMapper).eq(SysUser::getDelFlag, "F").listAs(SysDeptLeaderVO.LeaderInfoVO.class);
        SysDeptLeaderVO leaderVO = new SysDeptLeaderVO();
        leaderVO.setLeaderInfoVOS(userInfos);
        return leaderVO;
    }

    /**
     * 部门角色信息查询
     *
     * @param deptId 部门id
     */
    @Override
    public SysDeptRoleVO findSysDeptRole(Long deptId) {
        List<SysRole> sysRoleList = QueryChain.of(this.sysRoleMapper).list();
        List<SysDeptRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(sysRoleList, SysDeptRoleVO.RoleInfoVO.class);
        List<SysDeptRole> deptRoles = QueryChain.of(sysDeptRoleMapper).eq(SysDeptRole::getDeptId, deptId).list();
        List<Long> roleIds = new ArrayList<>();
        if (Utils.isNotNull(deptRoles)) {
            roleIds = deptRoles.stream().map(SysDeptRole::getRoleId).collect(Collectors.toList());
        }
        SysDeptRoleVO sysDeptRoleVO = new SysDeptRoleVO();
        sysDeptRoleVO.setRoleInfoVOS(roleInfoVOS);
        sysDeptRoleVO.setSelectIds(roleIds);
        return sysDeptRoleVO;
    }

    @Override
    public void changeSysDeptRole(SysDeptRoleDTO dto) {
        Long deptId = dto.getDeptId();
        List<Long> roleIds = dto.getRoleIds();
        // 删除当前部门下的所有角色
        UpdateChain.of(sysDeptRoleMapper).eq(SysDeptRole::getDeptId, deptId).remove();

        if (Utils.isNotNull(roleIds)) {
            List<SysDeptRole> deptRoles = dto.getRoleIds().stream().map(roleId -> {
                SysDeptRole sysDataRole = new SysDeptRole();
                sysDataRole.setRoleId(roleId);
                sysDataRole.setDeptId(deptId);
                return sysDataRole;
            }).toList();
            sysDeptRoleMapper.insertBatch(deptRoles);

            // 查询当前部门下所有用户
            List<SysUserDept> userDepts = QueryChain.of(sysUserDeptMapper).eq(SysUserDept::getDeptId, deptId).list();
            List<Long> userIdList = userDepts.stream().map(SysUserDept::getUserId).distinct().toList();
            if (userIdList.isEmpty()) {
                return;
            }

            // 查询并插入用户需要绑定的新角色
            List<SysUserRole> userRoleList = userRoleLinkData(userIdList, roleIds);
            if (Utils.isNotNull(userRoleList)) {
                sysUserRoleMapper.insertBatch(userRoleList);
                List<Long> changeUserIds = userRoleList.stream().map(SysUserRole::getUserId).distinct().toList();
                // 触发更新用户元数据事件
                eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(changeUserIds)));
            }
        }
    }

    /**
     * 构造用户角色关联数据
     */
    private List<SysUserRole> userRoleLinkData(List<Long> userIdList, List<Long> roleIds) {
        // 查询当前用户已有的角色
        List<SysUserRole> existingUserRoles = QueryChain.of(sysUserRoleMapper).in(SysUserRole::getUserId, userIdList).list();

        // 将用户角色关联信息作为key
        Set<String> existingBindings = existingUserRoles.stream().map(ur -> ur.getUserId() + "_" + ur.getRoleId()).collect(Collectors.toSet());
        List<SysUserRole> newUserRoles = new ArrayList<>();
        // 循环判断色插用户需要绑定的角色
        for (Long userId : userIdList) {
            for (Long roleId : roleIds) {
                // 检查绑定是否已存在
                String bindingKey = userId + "_" + roleId;
                if (!existingBindings.contains(bindingKey)) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    newUserRoles.add(userRole);
                }
            }
        }
        return newUserRoles;
    }

    private static QueryWrapper buildQueryWrapper(SysDeptListDTO dto) {
        return QueryWrapper.create().from(SysDept.class);
    }

    @Override
    public List<DepartmentVO> listSelector(SelectorQueryDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DEPT.ID, SYS_DEPT.PID, SYS_DEPT.NAME, SYS_DEPT.SORT, SYS_DEPT.DEEP)
                .orderBy(SYS_DEPT.SORT.asc());
        // 获取所有部门信息
        List<DepartmentVO> list = listAs(wrapper, DepartmentVO.class);
        return TreeUtils.buildTree(list, 0);
    }

    @Override
    public List<DeptOptionsVO> getDeptOptions() {
        QueryWrapper wrapper = QueryWrapper.create().select(SYS_DEPT.ID, SYS_DEPT.NAME).from(SYS_DEPT).orderBy(SYS_DEPT.SORT.asc());
        return listAs(wrapper, DeptOptionsVO.class);
    }

}