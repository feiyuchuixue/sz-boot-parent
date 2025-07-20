package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.sysfile.PreSignedUploadRequestDTO;
import com.sz.admin.system.pojo.dto.sysfile.PreSignedUploadResponseDTO;
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

    /**
     * 获取预签名上传信息
     *
     * @param dto 预签名上传请求DTO
     * @return 预签名上传响应DTO
     */
    PreSignedUploadResponseDTO getPreSignedUploadInfo(PreSignedUploadRequestDTO dto);

    /**
     * 确认上传完成
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    UploadResult confirmUploadComplete(Long fileId);

    /**
     * 获取文件URL
     * @param id 文件ID
     * @return URL
     */
    String getFileUrl(Long id);

    /**
     * 清理过期的上传记录
     */
    void cleanExpiredUploads();
}
