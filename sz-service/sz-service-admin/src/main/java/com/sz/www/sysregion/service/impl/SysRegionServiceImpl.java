package com.sz.www.sysregion.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.www.sysregion.service.SysRegionService;
import com.sz.www.sysregion.pojo.po.SysRegion;
import com.sz.www.sysregion.mapper.SysRegionMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;

import com.sz.www.sysregion.pojo.dto.SysRegionListDTO;
import org.springframework.web.multipart.MultipartFile;
import com.sz.excel.core.ExcelResult;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;

import com.sz.www.sysregion.pojo.vo.SysRegionVO;

/**
 * <p>
 * 系统行政区（字典）表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-04-25
 */
@Service
@RequiredArgsConstructor
public class SysRegionServiceImpl extends ServiceImpl<SysRegionMapper, SysRegion> implements SysRegionService {

    @Override
    public PageResult<SysRegionVO> page(SysRegionListDTO dto){
        Page<SysRegionVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysRegionVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysRegionVO> list(SysRegionListDTO dto){
        return listAs(buildQueryWrapper(dto), SysRegionVO.class);
    }

    private static QueryWrapper buildQueryWrapper(SysRegionListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysRegion.class);
        wrapper.like(SysRegion::getRegionName, dto.getRegionName());
        return wrapper;
    }
}