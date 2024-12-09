package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.CommonFileMapper;
import com.sz.admin.system.pojo.dto.sysfile.SysFileListDTO;
import com.sz.admin.system.pojo.po.SysFile;
import com.sz.admin.system.pojo.po.table.SysFileTableDef;
import com.sz.admin.system.service.SysFileService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.oss.OssClient;
import com.sz.oss.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-08-31
 */
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<CommonFileMapper, SysFile> implements SysFileService {

    private final OssClient ossClient;

    /**
     * 文件列表
     *
     * @param dto
     *            dto
     * @return {@link PageResult}<{@link SysFile}>
     */
    @Override
    public PageResult<SysFile> fileList(SysFileListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getFilename())) {
            wrapper.where(SysFileTableDef.SYS_FILE.FILENAME.like(dto.getFilename()));
        }
        wrapper.orderBy(SysFileTableDef.SYS_FILE.CREATE_TIME.desc());
        Page<SysFile> page = page(PageUtils.getPage(dto), wrapper);
        return PageUtils.getPageResult(page);
    }

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @param dirTag
     * @return {@link String}
     */
    @Override
    public UploadResult uploadFile(MultipartFile file, String dirTag) {
        UploadResult uploadResult = null;
        try {
            uploadResult = ossClient.upload(file, dirTag);
            Long fileId = fileLog(uploadResult);
            uploadResult.setFileId(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            CommonResponseEnum.FILE_UPLOAD_ERROR.assertTrue(true);
        }
        return uploadResult;
    }

    @Override
    public Long fileLog(UploadResult uploadResult) {
        SysFile sysFile = BeanCopyUtils.copy(uploadResult, SysFile.class);
        this.save(sysFile);
        return sysFile.getId();
    }
}
