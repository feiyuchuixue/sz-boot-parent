package com.sz.admin.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysDataScopeMapper;
import com.sz.admin.system.pojo.dto.sysdept.SysDeptListDTO;
import com.sz.admin.system.pojo.po.SysDataScope;
import com.sz.admin.system.pojo.vo.sysdatascope.SysDataScopeVO;
import com.sz.admin.system.pojo.vo.sysdept.SysDeptVO;
import com.sz.admin.system.service.SysDataScopeService;
import com.sz.admin.system.service.SysDeptService;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sz.admin.sysdatascope.pojo.po.table.SysDataScopeTableDef.SYS_DATA_SCOPE;


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

    private final SysDeptService SysDeptService;

    @Override
    public SysDataScopeVO findDeptDataScope(Integer relationId, String relationTypeCd) {
        SysDataScopeVO sysDataScopeVO = new SysDataScopeVO();
        List<SysDeptVO> deptVOS = SysDeptService.list(new SysDeptListDTO());
        sysDataScopeVO.setDeptLists(deptVOS);
        CommonResponseEnum.INVALID.assertFalse(Utils.isNotNull(relationTypeCd));
        QueryWrapper.create().where(SYS_DATA_SCOPE.RELATION_TYPE_CD.eq(relationTypeCd)).and(SYS_DATA_SCOPE.RELATION_ID.eq(relationId));
        // listAs()
        // Todo:  get json option
        return sysDataScopeVO;
    }

}