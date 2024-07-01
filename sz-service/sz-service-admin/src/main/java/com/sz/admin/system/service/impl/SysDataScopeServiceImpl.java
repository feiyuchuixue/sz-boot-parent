package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDataScopeMapper;
import com.sz.admin.system.pojo.dto.sysdatascope.SysDataScopeUpdateDTO;
import com.sz.admin.system.pojo.po.SysDataScope;
import com.sz.admin.system.service.SysDataScopeService;
import com.sz.core.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sz.admin.system.pojo.po.table.SysDataScopeTableDef.SYS_DATA_SCOPE;


/**
 * <p>
 * 自定义数据权限表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2024-06-27
 */
@Service
@RequiredArgsConstructor
public class SysDataScopeServiceImpl extends ServiceImpl<SysDataScopeMapper, SysDataScope> implements SysDataScopeService {

    //private final SysDeptService SysDeptService;

/*    @Override
    public SysDataScopeVO findDeptDataScope(Integer relationId, String relationTypeCd) {
        SysDataScopeVO sysDataScopeVO = new SysDataScopeVO();
        List<SysDeptVO> deptVOS = SysDeptService.list(new SysDeptListDTO());
        sysDataScopeVO.setDeptLists(deptVOS);
        CommonResponseEnum.INVALID.assertFalse(Utils.isNotNull(relationTypeCd));
        QueryWrapper.create().where(SYS_DATA_SCOPE.RELATION_TYPE_CD.eq(relationTypeCd)).and(SYS_DATA_SCOPE.RELATION_ID.eq(relationId));
        // listAs()
        // Todo:  get json option
        return sysDataScopeVO;
    }*/

    @Override
    public SysDataScope getJsonOption(Integer relationId, String relationTypeCd) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_SCOPE.RELATION_TYPE_CD.eq(relationTypeCd)).and(SYS_DATA_SCOPE.RELATION_ID.eq(relationId));
        return getOne(wrapper);
    }

    @Override
    public void updateDataScope(SysDataScopeUpdateDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SYS_DATA_SCOPE.RELATION_TYPE_CD.eq(dto.getRelationTypeCd())).and(SYS_DATA_SCOPE.RELATION_ID.eq(dto.getRelationId()));
        SysDataScope one = getOne(wrapper);
        if (one == null) {
            SysDataScope dataScope = BeanCopyUtils.copy(dto, SysDataScope.class);
            save(dataScope);
        }else {
         one.setUserOptions(dto.getUserOptions());
         one.setDeptOptions(dto.getDeptOptions());
         updateById(one);
        }
    }


}