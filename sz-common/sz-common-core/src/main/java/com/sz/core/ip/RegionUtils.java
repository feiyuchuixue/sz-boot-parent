package com.sz.core.ip;

import com.sz.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.InputStream;

/**
 * 根据ip地址定位工具类，离线方式
 *
 * @author sz
 */
@Slf4j
public class RegionUtils {

    // IP地址库文件名称
    public static final String IP_XDB_FILENAME = "region/ip2region.xdb";

    private static final Searcher SEARCHER;

    static {
        try {
            InputStream inputStream = RegionUtils.class.getClassLoader().getResourceAsStream(IP_XDB_FILENAME);
            if (inputStream == null) {
                // 替换为项目自定义异常
                throw new RuntimeException("IP地址库文件不存在");
            }
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            SEARCHER = Searcher.newWithBuffer(data);
            log.info("RegionUtils初始化成功，加载IP地址库数据成功！");
        } catch (Exception e) {
            throw new RuntimeException("RegionUtils初始化失败，原因：" + e.getMessage());
        }
    }

    /**
     * 根据IP地址离线获取城市
     */
    public static String getCityInfo(String ip) {
        try {
            // 3、执行查询
            String region = SEARCHER.search(StringUtils.trim(ip));
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip);
            return "未知";
        }
    }

}
