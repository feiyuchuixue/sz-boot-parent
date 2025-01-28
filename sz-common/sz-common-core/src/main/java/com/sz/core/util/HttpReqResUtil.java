package com.sz.core.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sz
 * @since 2023/2/21 10:04
 */
@Slf4j
public class HttpReqResUtil {

    private HttpReqResUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取客户端的IP地址。
     * <p>
     * 通过多种方式尝试获取客户端的真实IP地址，考虑了反向代理和多层代理的情况。
     * </p>
     *
     * @param request
     *            HttpServletRequest 对象
     * @return 客户端的IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = getHeaderIp(request, "X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        ip = getHeaderIp(request, "X-Real-IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        ip = getHeaderIp(request, "Proxy-Client-IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        ip = getHeaderIp(request, "WL-Proxy-Client-IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        ip = getHeaderIp(request, "HTTP_CLIENT_IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        ip = getHeaderIp(request, "HTTP_X_FORWARDED_FOR");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    private static String getHeaderIp(HttpServletRequest request, String header) {
        String ip = request.getHeader(header);
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return null;
    }

    /**
     * 将URL参数字符串转换为Map。
     * <p>
     * 解析URL参数，将键值对存储在Map中。如果参数中有键没有值，则值为空字符串。
     * </p>
     *
     * @param param
     *            包含URL参数的字符串
     * @return 包含参数键值对的Map
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<>();
        if (param != null && !param.isEmpty()) {
            String[] params = param.split("&");
            for (String paramPair : params) {
                String[] p = paramPair.split("=", 2); // Limit the split to 2 parts
                if (p.length == 2) {
                    String key = URLDecoder.decode(p[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(p[1], StandardCharsets.UTF_8);
                    map.put(key, value);
                } else if (p.length == 1) {
                    // Handle case where there is a key without a value
                    String key = URLDecoder.decode(p[0], StandardCharsets.UTF_8);
                    map.put(key, "");
                }
            }
        }
        return map;
    }

    /**
     * 获取请求体中的内容。
     * <p>
     * 读取并返回请求体中的所有内容，移除空格和换行符。
     * </p>
     *
     * @param request
     *            ServletRequest 对象
     * @return 请求体中的字符串内容
     */
    public static String getBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = request.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            log.error("Error reading the request body...", ex);
        }

        return stringBuilder.toString().replace(" ", "").replace("\\r\\n", "");
    }

    /**
     * 获取表单参数并转换为Map。
     * <p>
     * 遍历请求中的所有表单参数，将它们存储在Map中。
     * </p>
     *
     * @param request
     *            ServletRequest 对象
     * @return 包含表单参数的Map
     */
    public static Map<String, Object> getParameter(ServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }
        return paramMap;
    }

}
