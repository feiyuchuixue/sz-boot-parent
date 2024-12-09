package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysfile.SysFileListDTO;
import com.sz.admin.system.pojo.po.SysFile;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.oss.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sz
 * @since 2023-08-31
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 文件列表
     *
     * @param dto
     *            dto
     * @return {@link List}<{@link SysFile}>
     */
    PageResult<SysFile> fileList(SysFileListDTO dto);

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @return {@link ApiResult}
     */
    UploadResult uploadFile(MultipartFile file, String dirTag);

    Long fileLog(UploadResult uploadResult);
}
