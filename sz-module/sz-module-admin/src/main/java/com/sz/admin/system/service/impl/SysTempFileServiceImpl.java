package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.SysTempFileMapper;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileListDTO;
import com.sz.admin.system.pojo.dto.systempfile.SysTempFileUpdateDTO;
import com.sz.admin.system.pojo.po.SysTempFile;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileInfoVO;
import com.sz.admin.system.pojo.vo.systempfile.SysTempFileVO;
import com.sz.admin.system.service.SysFileService;
import com.sz.admin.system.service.SysTempFileHistoryService;
import com.sz.admin.system.service.SysTempFileService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.UploadResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.oss.OssClient;
import com.sz.resource.model.ResourceRef;
import com.sz.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import static com.sz.admin.system.pojo.po.table.SysResourceTableDef.SYS_RESOURCE;
import static com.sz.admin.system.pojo.po.table.SysTempFileTableDef.SYS_TEMP_FILE;

/**
 * <p>
 * 模版文件表 服务实现类
 * </p>
 *
 * @author sz
 * @since 2024-12-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysTempFileServiceImpl extends ServiceImpl<SysTempFileMapper, SysTempFile> implements SysTempFileService {

    private final OssClient ossClient;

    private final SysFileService sysFileService;

    private final SysTempFileHistoryService sysTempFileHistoryService;

    private final ResourceService resourceService;

    @Override
    public void create(SysTempFileCreateDTO dto) {
        SysTempFile sysTempFile = BeanCopyUtils.copy(dto, SysTempFile.class);
        // 唯一性校验
        long count = QueryChain.of(SysTempFile.class).eq(SysTempFile::getAlias, dto.getAlias()).count();
        CommonResponseEnum.EXISTS.message("标识：" + dto.getAlias() + " 已存在").assertTrue(count > 0);
        List<ResourceRef> url = dto.getUrl();
        Long fileId = url.getFirst().getResourceId();
        sysTempFile.setSysFileId(fileId);
        save(sysTempFile);
        SysTempFileHistoryCreateDTO history = BeanCopyUtils.copy(sysTempFile, SysTempFileHistoryCreateDTO.class);
        history.setSysTempFileId(sysTempFile.getId());
        sysTempFileHistoryService.create(history);
    }

    @Override
    public void update(SysTempFileUpdateDTO dto) {
        SysTempFile sysTempFile = BeanCopyUtils.copy(dto, SysTempFile.class);
        List<ResourceRef> url = dto.getUrl();
        Long fileId = url.getFirst().getResourceId();
        sysTempFile.setSysFileId(fileId);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysTempFile::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        // 唯一性校验
        long count;
        count = QueryChain.of(SysTempFile.class).eq(SysTempFile::getAlias, dto.getAlias()).ne(SysTempFile::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("标识：" + dto.getAlias() + " 已存在").assertTrue(count > 0);

        saveOrUpdate(sysTempFile);

        SysTempFileHistoryCreateDTO history = BeanCopyUtils.copy(sysTempFile, SysTempFileHistoryCreateDTO.class);
        history.setSysTempFileId(sysTempFile.getId());
        sysTempFileHistoryService.create(history);
    }

    @Override
    public PageResult<SysTempFileVO> page(SysTempFileListDTO dto) {
        Page<SysTempFileVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysTempFileVO.class);
        PageResult<SysTempFileVO> pageResult = PageUtils.getPageResult(page);
        pageResult.getRows().forEach(this::fillAccessUrl);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysTempFileVO> list(SysTempFileListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysTempFileVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SysTempFileVO detail(Object id) {
        SysTempFile sysTempFile = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysTempFile);
        SysTempFileVO vo = BeanCopyUtils.copy(sysTempFile, SysTempFileVO.class);
        fillAccessUrl(vo);
        return vo;
    }

    @Deprecated(since = "v1.4.0-beta", forRemoval = true)
    @Override
    public UploadResult uploadFile(MultipartFile file) {
        UploadResult uploadResult = null;
        try {
            uploadResult = ossClient.upload(file, "tmp", "");
            Long fileId = sysFileService.fileLog(uploadResult);
            uploadResult.setFileId(fileId);
        } catch (Exception e) {
            log.error(" sysTempFile oss upload error", e);
            CommonResponseEnum.FILE_UPLOAD_ERROR.assertTrue(true);
        }
        return uploadResult;
    }

    private static QueryWrapper buildQueryWrapper(SysTempFileListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysTempFile.class);
        if (Utils.isNotNull(dto.getTempName())) {
            wrapper.like(SysTempFile::getTempName, dto.getTempName());
        }
        return wrapper;
    }

    @Override
    public SysTempFileInfoVO detailByNameOrAlias(String tempName, String alias) {
        QueryWrapper wrapper = QueryWrapper.create().from(SYS_TEMP_FILE).leftJoin(SYS_RESOURCE).on(SYS_TEMP_FILE.SYS_FILE_ID.eq(SYS_RESOURCE.ID))
                .where(SYS_TEMP_FILE.TEMP_NAME.eq(tempName).or(SYS_TEMP_FILE.ALIAS.eq(alias)));
        return getOneAs(wrapper, SysTempFileInfoVO.class);
    }

    private void fillAccessUrl(SysTempFileVO vo) {
        resourceService.fillAccessUrl(vo.getUrl());
    }

}