package com.sz.configuration;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author sz
 * @since 2022/8/26 15:14
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {"com.sz.ssoadmin.**.mapper"})
public class MapperConfiguration {
}
