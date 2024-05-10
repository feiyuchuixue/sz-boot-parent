package com.sz.www.sysregion.service;

import com.mybatisflex.core.service.IService;
import com.sz.www.sysregion.pojo.po.SysRegion;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

import com.sz.www.sysregion.pojo.dto.SysRegionListDTO;
import com.sz.www.sysregion.pojo.vo.SysRegionVO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 系统行政区（字典）表 Service
 * </p>
 *
 * @author sz
 * @since 2024-04-25
 */
public interface SysRegionService extends IService<SysRegion> {


    PageResult<SysRegionVO> page(SysRegionListDTO dto);

    List<SysRegionVO> list(SysRegionListDTO dto);

}
