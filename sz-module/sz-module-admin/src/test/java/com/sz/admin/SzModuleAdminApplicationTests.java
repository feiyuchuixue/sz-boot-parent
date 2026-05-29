package com.sz.admin;

import com.sz.config.AdminMapperScanConfiguration;
import com.sz.config.ExcelPackageScanConfiguration;
import com.sz.excel.annotation.EnableExcelTemplateScan;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;

import static org.assertj.core.api.Assertions.assertThat;

class SzModuleAdminApplicationTests {

    @Test
    void adminModuleOwnsMapperScanConfiguration() {
        MapperScan mapperScan = AdminMapperScanConfiguration.class.getAnnotation(MapperScan.class);

        assertThat(mapperScan).isNotNull();
        assertThat(mapperScan.basePackages()).containsExactly("com.sz.admin.*.mapper", "com.sz.applet.*.mapper");
    }

    @Test
    void adminModuleOwnsExcelTemplateScanConfiguration() {
        EnableExcelTemplateScan excelTemplateScan = ExcelPackageScanConfiguration.class.getAnnotation(EnableExcelTemplateScan.class);

        assertThat(excelTemplateScan).isNotNull();
        assertThat(excelTemplateScan.basePackages()).containsExactly("com.sz.admin");
    }

}
