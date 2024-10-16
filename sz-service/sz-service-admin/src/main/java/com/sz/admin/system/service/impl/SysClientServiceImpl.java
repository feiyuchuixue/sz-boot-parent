package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysClientMapper;
import com.sz.admin.system.pojo.dto.sysclient.SysClientCreateDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientListDTO;
import com.sz.admin.system.pojo.dto.sysclient.SysClientUpdateDTO;
import com.sz.admin.system.pojo.po.SysClient;
import com.sz.admin.system.pojo.vo.sysclient.SysClientVO;
import com.sz.admin.system.service.SysClientService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.StreamUtils;
import com.sz.core.util.Utils;
import com.sz.security.pojo.ClientVO;
import com.sz.security.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统授权表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-01-22
 */
@Service
@RequiredArgsConstructor
public class SysClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements SysClientService, ClientService {

    @Override
    public void create(SysClientCreateDTO dto) {
        SysClient sysClient = BeanCopyUtils.springCopy(dto, SysClient.class);
        String clientId = Utils.generateUUIDs();
        sysClient.setClientId(clientId);
        sysClient.setClientSecret(Utils.generateUUIDs());
        // 唯一性校验
        QueryWrapper wrapper = QueryWrapper.create().eq(SysClient::getClientKey, dto.getClientKey());
        CommonResponseEnum.EXISTS.message("clientKey已存在").assertTrue(count(wrapper) > 0);
        List<String> grantTypeCdList = dto.getGrantTypeCdList();
        formatGrantType(grantTypeCdList, sysClient);
        save(sysClient);
    }

    @Override
    public void update(SysClientUpdateDTO dto) {
        SysClient sysClient = BeanCopyUtils.springCopy(dto, SysClient.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysClient::getClientId, dto.getClientId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        List<String> grantTypeCdList = dto.getGrantTypeCdList();
        formatGrantType(grantTypeCdList, sysClient);

        saveOrUpdate(sysClient);
    }

    @Override
    public PageResult<SysClientVO> page(SysClientListDTO dto) {
        Page<SysClientVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysClientVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysClientVO> list(SysClientListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysClientVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ClientVO detail(Object id) {
        SysClient sysClient = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysClient);
        ClientVO clientVO = BeanCopyUtils.springCopy(sysClient, ClientVO.class);
        clientVO.setGrantTypeCdList(Arrays.asList(sysClient.getGrantTypeCd().split(",")));
        return clientVO;
    }

    @Override
    public ClientVO getClientByClientId(Object id) {
        SysClient sysClient = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysClient);
        ClientVO clientVO = BeanCopyUtils.springCopy(sysClient, ClientVO.class);
        clientVO.setGrantTypeCdList(Arrays.asList(sysClient.getGrantTypeCd().split(",")));
        return clientVO;
    }

    private static QueryWrapper buildQueryWrapper(SysClientListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysClient.class);
        if (Utils.isNotNull(dto.getClientKey())) {
            wrapper.eq(SysClient::getClientKey, dto.getClientKey());
        }

        if (Utils.isNotNull(dto.getClientSecret())) {
            wrapper.eq(SysClient::getClientSecret, dto.getClientSecret());
        }

        if (Utils.isNotNull(dto.getGrantTypeCd())) {
            wrapper.eq(SysClient::getGrantTypeCd, dto.getGrantTypeCd());
        }

        if (Utils.isNotNull(dto.getDeviceTypeCd())) {
            wrapper.eq(SysClient::getDeviceTypeCd, dto.getDeviceTypeCd());
        }

        if (Utils.isNotNull(dto.getActiveTimeout())) {
            wrapper.eq(SysClient::getActiveTimeout, dto.getActiveTimeout());
        }

        return wrapper;
    }

    private static void formatGrantType(List<String> grantTypeCdList, SysClient sysClient) {
        if (grantTypeCdList.size() > 0) {
            String grantTypes = StreamUtils.listToStr(grantTypeCdList);
            sysClient.setGrantTypeCd(grantTypes);
        }
    }

}