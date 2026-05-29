package com.sz.admin.system.business;

import com.mybatisflex.core.query.QueryWrapper;
import com.sz.admin.system.mapper.SysRoleMenuMapper;
import com.sz.admin.system.mapper.SysUserMapper;
import com.sz.admin.system.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.sz.admin.system.pojo.po.SysDataRoleRelation;
import com.sz.admin.system.pojo.po.SysRoleMenu;
import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.pojo.vo.sysmenu.MenuTreeVO;
import com.sz.admin.system.pojo.vo.sysrolemenu.SysRoleMenuVO;
import com.sz.admin.system.pojo.vo.sysuser.UserOptionVO;
import com.sz.admin.system.script.AdminScriptExportService;
import com.sz.admin.system.service.SysDataRoleRelationService;
import com.sz.admin.system.service.SysDeptService;
import com.sz.admin.system.service.SysMenuService;
import com.sz.admin.system.service.impl.SysRoleMenuServiceImpl;
import com.sz.admin.system.service.impl.SysDataRoleRelationServiceImpl;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.RoleMenuScopeVO;
import com.sz.core.common.event.EventPublisher;
import com.sz.db.permission.DataScopeConstant;
import com.sz.platform.constant.dict.DataScopeRelationTypeConstant;
import com.sz.platform.event.PermissionChangeEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataScopeBusinessAcceptanceIT {

    @Test
    void getUserScopeSkipsEmptyRolesAndSuperRole() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService);

        assertThat(service.getUserScope(null)).isEmpty();
        assertThat(service.getUserScope(List.of())).isEmpty();
        assertThat(service.getUserScope(List.of(GlobalConstant.SUPER_ROLE))).isEmpty();

        verify(relationService, never()).listByRoleIdsAndMenuIds(any(), anyList());
    }

    @Test
    void getUserScopeIgnoresNonNumericRoleCodesInsteadOfBreakingLoginScopeBuild() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService);

        assertThat(service.getUserScope(List.of("demo_role", "", "  "))).isEmpty();

        verify(relationService, never()).listByRoleIdsAndMenuIds(any(), anyList());
    }

    @Test
    void dataRoleRelationQueryIgnoresEmptyAndNonNumericRoleIds() {
        MockableSysDataRoleRelationService service = new MockableSysDataRoleRelationService();

        assertThat(service.listByRoleIdsAndMenuIds(null, List.of(10L))).isEmpty();
        assertThat(service.listByRoleIdsAndMenuIds(Arrays.asList("demo_role", "", null, " "), List.of(10L))).isEmpty();
        assertThat(service.queryCount).isZero();

        SysDataRoleRelation matched = relation(1L, 10L, DataScopeRelationTypeConstant.DEPT, 700L);
        service.result = List.of(matched);

        assertThat(service.listByRoleIdsAndMenuIds(Arrays.asList("1", "demo_role", " 2 "), List.of(10L))).containsExactly(matched);
        assertThat(service.queryCount).isEqualTo(1);
    }

    @Test
    void customRelationBatchSaveDeletesOnlySameRoleMenuAndRelationType() {
        MockableSysDataRoleRelationService service = new MockableSysDataRoleRelationService();
        service.countResult = 1;

        service.batchSave(9L, 22L, DataScopeRelationTypeConstant.USER, List.of(501L, 502L));

        assertThat(service.removeWrappers).hasSize(1);
        assertThat(service.removeWrappers.get(0).toSQL()).containsIgnoringCase("role_id").containsIgnoringCase("menu_id")
                .containsIgnoringCase("relation_type_cd");
        assertThat(service.savedRelations).hasSize(2);
        assertThat(service.savedRelations).allSatisfy(relation -> {
            assertThat(relation.getRoleId()).isEqualTo(9L);
            assertThat(relation.getMenuId()).isEqualTo(22L);
            assertThat(relation.getRelationTypeCd()).isEqualTo(DataScopeRelationTypeConstant.USER);
        });
        assertThat(service.savedRelations).extracting(SysDataRoleRelation::getRelationId).containsExactly(501L, 502L);
    }

    @Test
    void getUserScopeMergesNonCustomCustomAndAllScopesByBusinessPriority() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService);
        service.listResults.add(List.of(
                roleMenu(1L, 10L, DataScopeConstant.DEPT_ONLY),
                roleMenu(2L, 10L, DataScopeConstant.DEPT_AND_BELOW),
                roleMenu(1L, 20L, DataScopeConstant.CUSTOM),
                roleMenu(1L, 30L, DataScopeConstant.ALL),
                roleMenu(2L, 30L, DataScopeConstant.CUSTOM),
                roleMenu(1L, 40L, DataScopeConstant.SELF_ONLY),
                roleMenu(2L, 40L, DataScopeConstant.CUSTOM)));
        when(relationService.listByRoleIdsAndMenuIds(eq(List.of("1", "2")), anyList()))
                .thenReturn(List.of(relation(1L, 20L, DataScopeRelationTypeConstant.DEPT, 700L),
                        relation(1L, 20L, DataScopeRelationTypeConstant.USER, 800L),
                        relation(2L, 40L, DataScopeRelationTypeConstant.DEPT, 900L),
                        relation(2L, 40L, DataScopeRelationTypeConstant.USER, 901L)));

        Map<Long, RoleMenuScopeVO> result = service.getUserScope(List.of("1", "2"));

        assertThat(result).hasSize(4);
        assertThat(result.get(10L).getDataScopeCd()).isEqualTo(DataScopeConstant.DEPT_AND_BELOW);
        assertThat(result.get(10L).getCustomScope()).isNull();
        assertThat(result.get(20L).getDataScopeCd()).isEqualTo(DataScopeConstant.CUSTOM);
        assertThat(result.get(20L).getCustomScope().getDeptIds()).containsExactly(700L);
        assertThat(result.get(20L).getCustomScope().getUserIds()).containsExactly(800L);
        assertThat(result.get(30L).getDataScopeCd()).isEqualTo(DataScopeConstant.ALL);
        assertThat(result.get(30L).getExtraCustomScope()).isNull();
        assertThat(result.get(40L).getDataScopeCd()).isEqualTo(DataScopeConstant.SELF_ONLY);
        assertThat(result.get(40L).getExtraCustomScope().getDeptIds()).containsExactly(900L);
        assertThat(result.get(40L).getExtraCustomScope().getUserIds()).containsExactly(901L);
    }

    @Test
    void changeRoleMenuPersistsMenuScopesCustomRelationsAndPermissionChangeEvent() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        EventPublisher eventPublisher = mock(EventPublisher.class);
        SysRoleMenuMapper mapper = mock(SysRoleMenuMapper.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService, eventPublisher);
        ReflectionTestUtils.setField(service, "mapper", mapper);
        service.affectedUserIds = List.of("1001", "1002");

        SysRoleMenuDTO dto = roleMenuChangeDto();
        service.change(dto);

        ArgumentCaptor<Collection<SysRoleMenu>> menuCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(mapper).insertBatch(menuCaptor.capture());
        assertThat(menuCaptor.getValue()).extracting(SysRoleMenu::getMenuId).containsExactly(11L, 12L);
        assertThat(menuCaptor.getValue()).allSatisfy(item -> {
            assertThat(item.getRoleId()).isEqualTo(9L);
            assertThat(item.getPermissionType()).isEqualTo("menu");
            assertThat(item.getDataScopeCd()).isEqualTo("");
        });

        verify(relationService).deleteByRoleId(9L);
        verify(relationService).batchSave(9L, 22L, DataScopeRelationTypeConstant.USER, List.of(501L));
        verify(relationService).batchSave(9L, 22L, DataScopeRelationTypeConstant.DEPT, List.of(601L, 602L));
        assertThat(service.savedScopeMenus).extracting(SysRoleMenu::getMenuId).containsExactly(21L, 22L);
        assertThat(service.savedScopeMenus).extracting(SysRoleMenu::getDataScopeCd)
                .containsExactly(DataScopeConstant.DEPT_ONLY, DataScopeConstant.CUSTOM);

        ArgumentCaptor<PermissionChangeEvent> eventCaptor = ArgumentCaptor.forClass(PermissionChangeEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getPayload().getUserIds()).isEqualTo(List.of("1001", "1002"));
    }

    @Test
    void changeRoleMenuAllowsClearingAllMenusAndScopesWithoutNullPointer() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        EventPublisher eventPublisher = mock(EventPublisher.class);
        SysRoleMenuMapper mapper = mock(SysRoleMenuMapper.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService, eventPublisher);
        ReflectionTestUtils.setField(service, "mapper", mapper);
        service.affectedUserIds = List.of("1001");
        SysRoleMenuDTO dto = new SysRoleMenuDTO();
        dto.setRoleId(9L);

        service.change(dto);

        verify(relationService).deleteByRoleId(9L);
        verify(mapper, never()).insertBatch(any());
        assertThat(service.savedScopeMenus).isEmpty();
        ArgumentCaptor<PermissionChangeEvent> eventCaptor = ArgumentCaptor.forClass(PermissionChangeEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getPayload().getUserIds()).isEqualTo(List.of("1001"));
    }

    @Test
    void queryRoleMenuCombinesNormalAndCustomScopeSelections() {
        SysDataRoleRelationService relationService = mock(SysDataRoleRelationService.class);
        SysMenuService menuService = mock(SysMenuService.class);
        SysDeptService deptService = mock(SysDeptService.class);
        SysUserMapper userMapper = mock(SysUserMapper.class);
        MockableSysRoleMenuService service = new MockableSysRoleMenuService(relationService, mock(EventPublisher.class), menuService, deptService, userMapper);
        service.listResults.add(List.of(roleMenu(3L, 11L, "")));
        service.listResults.add(List.of(roleMenu(3L, 21L, DataScopeConstant.DEPT_AND_BELOW), roleMenu(3L, 22L, DataScopeConstant.CUSTOM)));
        when(menuService.queryRoleMenuTree(true)).thenReturn(List.of(new MenuTreeVO()));
        when(deptService.getDeptTree(null, false, false)).thenReturn(List.of(new DeptTreeVO()));
        when(userMapper.selectListByQueryAs(any(QueryWrapper.class), eq(UserOptionVO.class))).thenReturn(List.of(new UserOptionVO()));
        when(relationService.queryRelationByRoleIdAndMenuIds(3L, List.of(22L)))
                .thenReturn(List.of(relation(3L, 22L, DataScopeRelationTypeConstant.USER, 7001L),
                        relation(3L, 22L, DataScopeRelationTypeConstant.DEPT, 8001L)));

        SysRoleMenuVO result = service.queryRoleMenu(3L);

        assertThat(result.getMenuLists()).hasSize(1);
        assertThat(result.getDeptLists()).hasSize(1);
        assertThat(result.getUserLists()).hasSize(1);
        assertThat(result.getSelectMenuIds()).containsExactly(11L);
        assertThat(result.getScope()).hasSize(2);
        assertThat(result.getScope()).anySatisfy(scope -> {
            assertThat(scope.getMenuId()).isEqualTo(21L);
            assertThat(scope.getDataScope()).isEqualTo(DataScopeConstant.DEPT_AND_BELOW);
            assertThat(scope.getDeptIds()).isNull();
            assertThat(scope.getUserIds()).isNull();
        });
        assertThat(result.getScope()).anySatisfy(scope -> {
            assertThat(scope.getMenuId()).isEqualTo(22L);
            assertThat(scope.getDataScope()).isEqualTo(DataScopeConstant.CUSTOM);
            assertThat(scope.getDeptIds()).containsExactly(8001L);
            assertThat(scope.getUserIds()).containsExactly(7001L);
        });
    }

    private static SysRoleMenuDTO roleMenuChangeDto() {
        SysRoleMenuDTO dto = new SysRoleMenuDTO();
        dto.setRoleId(9L);
        SysRoleMenuDTO.Menu menu = new SysRoleMenuDTO.Menu();
        menu.setMenuIds(List.of(11L, 12L));
        dto.setMenu(menu);
        SysRoleMenuDTO.Scope normalScope = new SysRoleMenuDTO.Scope();
        normalScope.setMenuId(21L);
        normalScope.setDataScope(DataScopeConstant.DEPT_ONLY);
        SysRoleMenuDTO.Scope customScope = new SysRoleMenuDTO.Scope();
        customScope.setMenuId(22L);
        customScope.setDataScope(DataScopeConstant.CUSTOM);
        customScope.setUserIds(List.of(501L));
        customScope.setDeptIds(List.of(601L, 602L));
        dto.setScope(List.of(normalScope, customScope));
        return dto;
    }

    private static SysRoleMenu roleMenu(Long roleId, Long menuId, String dataScopeCd) {
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        roleMenu.setPermissionType("scope");
        roleMenu.setDataScopeCd(dataScopeCd);
        return roleMenu;
    }

    private static SysDataRoleRelation relation(Long roleId, Long menuId, String type, Long relationId) {
        SysDataRoleRelation relation = new SysDataRoleRelation();
        relation.setRoleId(roleId);
        relation.setMenuId(menuId);
        relation.setRelationTypeCd(type);
        relation.setRelationId(relationId);
        return relation;
    }

    private static class MockableSysRoleMenuService extends SysRoleMenuServiceImpl {

        private final ArrayDeque<List<SysRoleMenu>> listResults = new ArrayDeque<>();

        private List<String> affectedUserIds = List.of();

        private List<SysRoleMenu> savedScopeMenus = List.of();

        MockableSysRoleMenuService(SysDataRoleRelationService relationService) {
            this(relationService, mock(EventPublisher.class));
        }

        MockableSysRoleMenuService(SysDataRoleRelationService relationService, EventPublisher eventPublisher) {
            this(relationService, eventPublisher, mock(SysMenuService.class), mock(SysDeptService.class), mock(SysUserMapper.class));
        }

        MockableSysRoleMenuService(SysDataRoleRelationService relationService, EventPublisher eventPublisher, SysMenuService menuService,
                SysDeptService deptService, SysUserMapper userMapper) {
            super(menuService, eventPublisher, deptService, relationService, userMapper, mock(AdminScriptExportService.class));
        }

        @Override
        public <R> List<R> listAs(QueryWrapper queryWrapper, Class<R> asType) {
            return (List<R>) affectedUserIds;
        }

        @Override
        public boolean remove(QueryWrapper queryWrapper) {
            return true;
        }

        @Override
        public boolean saveBatch(Collection<SysRoleMenu> entityList) {
            savedScopeMenus = new ArrayList<>(entityList);
            return true;
        }

        @Override
        public List<SysRoleMenu> list(QueryWrapper queryWrapper) {
            return listResults.isEmpty() ? List.of() : listResults.removeFirst();
        }
    }

    private static class MockableSysDataRoleRelationService extends SysDataRoleRelationServiceImpl {

        private List<SysDataRoleRelation> result = List.of();

        private long countResult;

        private int queryCount;

        private final List<QueryWrapper> removeWrappers = new ArrayList<>();

        private Collection<SysDataRoleRelation> savedRelations = List.of();

        @Override
        public long count(QueryWrapper queryWrapper) {
            return countResult;
        }

        @Override
        public boolean remove(QueryWrapper queryWrapper) {
            removeWrappers.add(queryWrapper);
            return true;
        }

        @Override
        public boolean saveBatch(Collection<SysDataRoleRelation> entityList) {
            savedRelations = new ArrayList<>(entityList);
            return true;
        }

        @Override
        public List<SysDataRoleRelation> list(QueryWrapper queryWrapper) {
            queryCount++;
            return result;
        }
    }
}
