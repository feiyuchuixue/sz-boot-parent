package com.sz.core.ip;

import com.sz.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

import java.io.InputStream;

/**
 * 根据ip地址定位工具类，离线方式
 *
 * @author sz
 */
@Slf4j
public class RegionUtils {

    // IP地址库文件名称
    public static final String IP_XDB_CLASSPATH_V4 = "region/ip2region_v4.xdb";

    private static final Searcher SEARCHER;

    private static final Version version = Version.IPv4;

    static {
        LongByteArray cBuff = null;
        try (InputStream is = RegionUtils.class.getClassLoader().getResourceAsStream(IP_XDB_CLASSPATH_V4)) {
            if (is == null) {
                log.error("无法找到 ip2region 地址库文件: {}", IP_XDB_CLASSPATH_V4);
            } else {
                // 将 InputStream 转为 byte[]
                byte[] bytes = is.readAllBytes();
                cBuff = new LongByteArray(bytes);
            }
        } catch (Exception e) {
            log.error("加载ip地址库失败", e);
        }
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithBuffer(version, cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
            log.error("创建Region缓存搜索器失败", e);
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
