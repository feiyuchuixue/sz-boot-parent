package com.sz.core.util;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * FileUploadUtils
 *
 * @author zhy
 * @since 2023/8/30
 */
public class FileUploadUtils {

    /**
     * 生成文件名
     *
     * @param originalFilename
     *            原始文件名
     * @return {@link String}
     */
    public static String generateFileName(String originalFilename) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String localTime = sdf.format(new Date());
        String pathStr = "/" + localTime + "/";
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String fileName = UUID.randomUUID().toString();
        return pathStr + fileName + "." + extension;
    }

}
