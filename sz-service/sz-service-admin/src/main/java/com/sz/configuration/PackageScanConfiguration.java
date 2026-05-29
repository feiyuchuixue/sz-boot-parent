package com.sz.configuration;

import com.sz.excel.annotation.EnableExcelTemplateScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author sz
 * @since 2022/8/26 15:14
 */
@Slf4j
@Configuration
@EnableExcelTemplateScan(basePackages = {"com.sz.admin"})
public class PackageScanConfiguration {
}
