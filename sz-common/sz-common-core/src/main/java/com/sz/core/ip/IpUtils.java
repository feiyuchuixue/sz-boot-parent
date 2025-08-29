package com.sz.core.ip;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP地址工具类，提供IP地址验证、归属地查询等功能
 *
 * @author sz
 * @since 2025/7/28 13:45
 */
@Slf4j
public class IpUtils {

    // 判断ip地址正则
    public static final Pattern IPV4 = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$");

    public static final Pattern IPV6 = Pattern.compile(
            "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))");

    // 未知IP
    public static final String UNKNOWN_IP = "XX XX";

    // 内网地址
    public static final String LOCAL_ADDRESS = "内网IP";

    // 未知地址
    public static final String UNKNOWN_ADDRESS = "未知";

    /**
     * 根据IP地址获取真实归属地
     *
     * @param ip
     *            IP地址字符串
     * @return 归属地信息，内网IP返回{@link #LOCAL_ADDRESS}，不支持的IP类型返回{@link #UNKNOWN_IP}
     */
    public static String getRealAddressByIP(String ip) {
        // 判断是否为IPv4
        if (isIPv4(ip)) {
            // 内网不查询
            if (isInnerIP(ip)) {
                return LOCAL_ADDRESS;
            }
            return RegionUtils.getCityInfo(ip);
        }
        // 如果不是IPv4，则返回未知IP
        return UNKNOWN_IP;
    }

    /**
     * 根据IPv6地址查询IP归属行政区域
     * <p>
     * 目前ip2region不支持IPv6解析，直接返回未知地址
     * </p>
     *
     * @param ip
     *            ipv6地址
     * @return 归属行政区域，固定返回{@link #UNKNOWN_ADDRESS}
     */
    private static String resolverIPv6Region(String ip) {
        log.warn("ip2region不支持IPV6地址解析：{}", ip);
        // 不支持IPv6，直接返回
        return UNKNOWN_ADDRESS;
    }

    /**
     * 判断是否为IPv4地址
     *
     * @param ip
     *            IP地址字符串
     * @return true-是IPv4地址，false-不是IPv4地址
     */
    private static boolean isIPv4(String ip) {
        return ip != null && IPV4.matcher(ip).matches();
    }

    /**
     * 判断是否为IPv6地址
     *
     * @param ip
     *            IP地址字符串
     * @return true-是IPv6地址，false-不是IPv6地址
     */
    private static boolean isIPv6(String ip) {
        return ip != null && IPV6.matcher(ip).matches();
    }

    /**
     * 判断是否为内网IP地址
     * <p>
     * 支持10.0.0.0/8、172.16.0.0/12、192.168.0.0/16网段及127.0.0.1
     * </p>
     *
     * @param ipAddress
     *            IP地址字符串
     * @return true-是内网IP，false-不是内网IP
     */
    private static boolean isInnerIP(String ipAddress) {
        long ipNum = ipv4ToLong(ipAddress);
        long aBegin = ipv4ToLong("10.0.0.0");
        long aEnd = ipv4ToLong("10.255.255.255");
        long bBegin = ipv4ToLong("172.16.0.0");
        long bEnd = ipv4ToLong("172.31.255.255");
        long cBegin = ipv4ToLong("192.168.0.0");
        long cEnd = ipv4ToLong("192.168.255.255");
        return isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || "127.0.0.1".equals(ipAddress);
    }

    /**
     * 将IPv4地址转换为长整型数字
     *
     * @param strIP
     *            IPv4地址字符串
     * @return 转换后的长整型数字
     * @throws IllegalArgumentException
     *             如果IP地址格式不正确
     */
    private static long ipv4ToLong(String strIP) {
        Matcher matcher = IPV4.matcher(strIP);
        if (matcher.matches()) {
            return matchAddress(matcher);
        } else {
            throw new IllegalArgumentException("Invalid IPv4 address!");
        }
    }

    /**
     * 解析Matcher中的IP地址组并转换为长整型
     *
     * @param matcher
     *            已匹配IPv4地址的Matcher对象
     * @return 转换后的长整型IP地址
     */
    private static long matchAddress(Matcher matcher) {
        long addr = 0L;
        for (int i = 1; i <= 4; ++i) {
            addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
        }
        return addr;
    }

    /**
     * 判断IP数值是否在指定范围内
     *
     * @param userIp
     *            用户IP转换的长整型数值
     * @param begin
     *            起始IP数值
     * @param end
     *            结束IP数值
     * @return true-在范围内，false-不在范围内
     */
    private static boolean isInner(long userIp, long begin, long end) {
        return userIp >= begin && userIp <= end;
    }

}
