package com.sz.configuration;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author: sz
 * @date: 2022/8/26 15:14
 */
@Slf4j
@Configuration
@MapperScan({"com.sz.admin.*.mapper", "com.sz.generator.mapper", "com.sz.www.*.mapper", "com.sz.applet.*.mapper"})
public class MapperConfiguration {
}
