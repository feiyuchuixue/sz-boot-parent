package com.sz.core.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
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
 * @author: sz
 * @date: 2023/2/21 10:04
 * @description:
 */
public class HttpReqResUtil {

    /**
     * 获取Ip地址
     *
     * @param request
     *            HttpServletRequest
     * @return ip
     */
    public static String getIpAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

    /**
     * url参数
     *
     * @param param
     * @return
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
     * body参数
     *
     * @param request
     * @return
     */
    public static String getBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
            }
        } catch (IOException ex) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }
        return stringBuilder.toString().replaceAll(" ", "").replaceAll("\\r\\n", "");
    }

    /**
     * 表单参数
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameter(ServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> a = request.getParameterNames();
        String param = null;
        String val = "";
        while (a.hasMoreElements()) {
            // 参数名
            param = a.nextElement();
            // 值
            val = request.getParameter(param);
            paramMap.put(param, val);
        }
        return paramMap;
    }

}
