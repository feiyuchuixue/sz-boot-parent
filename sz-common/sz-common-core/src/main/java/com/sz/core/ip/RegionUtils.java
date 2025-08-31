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
        Searcher searcher = null;
        try (InputStream inputStream = RegionUtils.class.getClassLoader().getResourceAsStream(IP_XDB_FILENAME)) {
            if (inputStream == null) {
                throw new RuntimeException("IP地址库文件不存在: " + IP_XDB_FILENAME);
            }
            byte[] data = inputStream.readAllBytes();
            searcher = Searcher.newWithBuffer(data);
            log.info("RegionUtils初始化成功，加载IP地址库数据成功！");
        } catch (Exception e) {
            log.error("RegionUtils初始化失败", e);
        }
        SEARCHER = searcher;
    }

    /**
     * 根据IP地址离线获取城市
     */
    public static String getCityInfo(String ip) {
        if (SEARCHER == null) {
            log.error("IP地址库未初始化，无法查询IP: {}", ip);
            return "未知";
        }
        try {
            String region = SEARCHER.search(StringUtils.trim(ip));
            if (region == null) {
                log.warn("IP地址库未查询到结果: {}", ip);
                return "未知";
            }
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip, e);
            return "未知";
        }
    }

}
