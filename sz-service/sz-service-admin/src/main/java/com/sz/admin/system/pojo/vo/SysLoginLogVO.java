package com.sz.admin.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * SysLoginLog返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-07-25
 */
@Data
@Accessors(chain = true)
@Schema(description = "SysLoginLog返回vo")
public class SysLoginLogVO {

    @ExcelIgnore
    @Schema(description = "登陆ID")
    private Integer id;

    @ExcelProperty(value = "用户名")
    @Schema(description = "用户名")
    private String userName;

    @ExcelProperty(value = "登陆状态")
    @DictFormat(dictType = "login_status")
    @Schema(description = "登陆状态")
    private String loginStatus;

    @ExcelProperty(value = "登陆时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "登陆时间")
    private LocalDateTime loginTime;

    @ExcelProperty(value = "登陆ip地址")
    @Schema(description = "登陆ip地址")
    private String ipAddress;

    @ExcelProperty(value = "登陆地点")
    @Schema(description = "登陆地点")
    private String loginLocation;

    @ExcelProperty(value = "浏览器类型")
    @Schema(description = "浏览器类型")
    private String browser;

    @ExcelProperty(value = "操作系统")
    @Schema(description = "操作系统")
    private String os;

    @ExcelProperty(value = "提示消息")
    @Schema(description = "提示消息")
    private String msg;

    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

}