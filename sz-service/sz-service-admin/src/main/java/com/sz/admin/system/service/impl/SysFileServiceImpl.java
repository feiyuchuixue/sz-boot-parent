package com.sz.admin.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.system.mapper.CommonFileMapper;
import com.sz.admin.system.pojo.dto.sysfile.PreSignedUploadRequestDTO;
import com.sz.admin.system.pojo.dto.sysfile.PreSignedUploadResponseDTO;
import com.sz.admin.system.pojo.dto.sysfile.SysFileListDTO;
import com.sz.admin.system.pojo.enums.FileUploadStatus;
import com.sz.admin.system.pojo.po.SysFile;
import com.sz.admin.system.pojo.po.table.SysFileTableDef;
import com.sz.admin.system.service.SysFileService;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.PageUtils;
import com.sz.core.util.Utils;
import com.sz.oss.OssClient;
import com.sz.oss.OssFileInfo;
import com.sz.oss.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sz
 * @since 2023-08-31
 */
@Slf4j
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
     *            文件夹标识
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
            log.error(" sysFile oss upload error", e);
            CommonResponseEnum.FILE_UPLOAD_ERROR.assertTrue(true);
        }
        return uploadResult;
    }

    @Override
    public Long fileLog(UploadResult uploadResult) {
        SysFile sysFile = BeanCopyUtils.copy(uploadResult, SysFile.class);
        // 设置上传之后的信息
        sysFile.setStatus(FileUploadStatus.COMPLETED);
        sysFile.setUploadExpireTime(LocalDateTime.now());
        this.save(sysFile);
        return sysFile.getId();
    }


    @Override
    public PreSignedUploadResponseDTO getPreSignedUploadInfo(PreSignedUploadRequestDTO dto) {
        try {
            // 生成唯一的对象名
            String objectName = generateObjectName(dto.getFilename(), dto.getDirTag());

            // 生成预签名上传URL（5分钟有效期）
            String preSignedUrl = ossClient.generatePreSignedUploadUrl(objectName, 5);

            // 创建数据库记录，状态为上传中
            SysFile sysFile = new SysFile();
            sysFile.setFilename(dto.getFilename());
            sysFile.setDirTag(dto.getDirTag());
            sysFile.setSize(dto.getFileSize());
            sysFile.setObjectName(objectName);
            sysFile.setContextType(dto.getContentType());
            sysFile.setStatus(FileUploadStatus.UPLOADING);
            sysFile.setUploadExpireTime(LocalDateTime.now().plusMinutes(5));

            this.save(sysFile);

            // 构建响应
            PreSignedUploadResponseDTO response = new PreSignedUploadResponseDTO();
            response.setFileId(sysFile.getId());
            response.setUploadUrl(preSignedUrl);
            response.setObjectName(objectName);
            response.setExpireTime(sysFile.getUploadExpireTime());

            return response;
        } catch (Exception e) {
            log.error("获取预签名上传信息失败", e);
            throw new RuntimeException("获取预签名上传信息失败");
        }
    }

    @Override
    public UploadResult confirmUploadComplete(Long fileId) {
        // 查询文件记录
        SysFile sysFile = this.getById(fileId);
        CommonResponseEnum.INVALID_ID.assertNull(sysFile);

        CommonResponseEnum.FILE_UPLOAD_ERROR.assertFalse(FileUploadStatus.UPLOADING.equals(sysFile.getStatus()));

        // 检查上传是否过期
        // 这里不需要判断了，假设是上传时间很久，实际上是可以上传的，后端不需要管是否过期，oss才会去考虑这个
//        if (LocalDateTime.now().isAfter(sysFile.getUploadExpireTime())) {
//            // 标记为失败
//            sysFile.setStatus(FileUploadStatus.FAILED);
//            this.updateById(sysFile);
//            log.warn("上传已过期，fileId: {}", fileId);
//            throw CommonResponseEnum.FILE_UPLOAD_STATUS_ERROR.newException();
//        }

        // 检查OSS中文件是否存在
        if (!ossClient.isFileExists(sysFile.getObjectName())) {
            log.warn("OSS中文件不存在，objectName: {}, fileId: {}", sysFile.getObjectName(), fileId);
            throw CommonResponseEnum.FILE_UPLOAD_ERROR.newException();
        }

        // 获取文件完整信息
        OssFileInfo fileInfo = ossClient.getFileInfo(sysFile.getObjectName());

        // 更新文件记录
        sysFile.setStatus(FileUploadStatus.COMPLETED);
        sysFile.setUrl(fileInfo.getUrl());
        sysFile.setSize(fileInfo.getSize());
        sysFile.setETag(fileInfo.getETag());

        this.updateById(sysFile);

        log.info("文件上传确认完成，fileId: {}", fileId);
        return UploadResult.builder()
                .fileId(sysFile.getId())
                .url(sysFile.getUrl())
                .filename(sysFile.getFilename())
                .contextType(sysFile.getContextType())
                .size(sysFile.getSize())
                .eTag(sysFile.getETag())
                .objectName(sysFile.getObjectName())
                .dirTag(sysFile.getDirTag())
                .build();
    }

    @Override
    public String getFileUrl(Long id) {
        if (id == null) {
            return null;
        }
        return this.queryChain()
                .select(SysFileTableDef.SYS_FILE.URL)
                .eq(SysFile::getId, id)
                .oneAs(String.class);
    }

    /**
     * 生成对象名称
     */
    private String generateObjectName(String filename, String dirTag) {
        // 使用时间戳和UUID生成唯一文件名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomStr = java.util.UUID.randomUUID().toString().replace("-", "");
        String extension = "";

        int lastDot = filename.lastIndexOf(".");
        if (lastDot > 0) {
            extension = filename.substring(lastDot);
        }

        // 格式：dirTag/timestamp_random.ext
        return dirTag + "/" + timestamp + "_" + randomStr + extension;
    }

    /**
     * 清理过期的上传记录
     * 每小时执行一次，检查已过期的上传项
     */
    @Override
    @Scheduled(cron = "0 0 * * * ?") // 每小时的0分0秒执行
    public void cleanExpiredUploads() {
        try {
            // 给10分钟缓冲时间，避免正在上传的文件被误判
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);

            // 查询状态为UPLOADING且过期时间早于cutoffTime的记录
            QueryWrapper wrapper = QueryWrapper.create()
                    .where(SysFileTableDef.SYS_FILE.STATUS.eq(FileUploadStatus.UPLOADING))
                    .and(SysFileTableDef.SYS_FILE.UPLOAD_EXPIRE_TIME.lt(cutoffTime));

            List<SysFile> expiredFiles = this.list(wrapper);

            if (expiredFiles.isEmpty()) {
                log.debug("没有发现过期的上传记录");
                return;
            }

            log.info("发现 {} 个过期的上传记录，开始检查", expiredFiles.size());

            int successCount = 0;
            int failedCount = 0;

            for (SysFile sysFile : expiredFiles) {
                try {
                    // 检查OSS中文件是否存在
                    if (ossClient.isFileExists(sysFile.getObjectName())) {
                        // 文件存在，获取完整信息并标记为完成
                        OssFileInfo fileInfo = ossClient.getFileInfo(sysFile.getObjectName());

                        sysFile.setStatus(FileUploadStatus.COMPLETED);
                        sysFile.setUrl(fileInfo.getUrl());
                        sysFile.setSize(fileInfo.getSize());
                        sysFile.setETag(fileInfo.getETag());

                        this.updateById(sysFile);
                        successCount++;

                        log.info("过期文件上传成功确认，fileId: {}, objectName: {}",
                                sysFile.getId(), sysFile.getObjectName());
                    } else {
                        // 文件不存在，标记为失败
                        sysFile.setStatus(FileUploadStatus.FAILED);
                        this.updateById(sysFile);
                        failedCount++;

                        log.warn("过期文件上传失败，fileId: {}, objectName: {}",
                                sysFile.getId(), sysFile.getObjectName());
                    }
                } catch (Exception e) {
                    log.error("处理过期文件记录失败，fileId: {}, objectName: {}",
                            sysFile.getId(), sysFile.getObjectName(), e);

                    // 出现异常时也标记为失败
                    try {
                        sysFile.setStatus(FileUploadStatus.FAILED);
                        this.updateById(sysFile);
                        failedCount++;
                    } catch (Exception updateEx) {
                        log.error("更新文件状态为失败时出错，fileId: {}", sysFile.getId(), updateEx);
                    }
                }
            }

            log.info("过期上传记录清理完成，成功确认: {}, 标记失败: {}", successCount, failedCount);

        } catch (Exception e) {
            log.error("清理过期上传记录时发生异常", e);
        }
    }

}
