package com.sz.admin.system.business;

import cn.idev.excel.annotation.ExcelIgnore;
import com.sz.admin.system.controller.CommonController;
import com.sz.admin.system.service.CommonService;
import com.sz.admin.teacher.pojo.dto.TeacherStatisticsImportDTO;
import com.sz.config.ExcelPackageScanConfiguration;
import com.sz.excel.annotation.DictFormat;
import com.sz.excel.annotation.EnableExcelTemplateScan;
import com.sz.excel.annotation.ExcelEnumFormat;
import com.sz.excel.annotation.ExcelTemplate;
import com.sz.excel.annotation.ImportColumn;
import com.sz.excel.enums.ExcelEnumPreset;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExcelBusinessAcceptanceIT {

    @Test
    void templateScanAndImportDtoDefineRequiredTemplateColumns() throws NoSuchFieldException {
        EnableExcelTemplateScan scan = ExcelPackageScanConfiguration.class.getAnnotation(EnableExcelTemplateScan.class);
        ExcelTemplate template = TeacherStatisticsImportDTO.class.getAnnotation(ExcelTemplate.class);

        assertThat(scan.basePackages()).containsExactly("com.sz.admin");
        assertThat(template.alias()).endsWith(".xlsx");
        assertImportColumn("year", true, 100);
        assertImportColumn("month", true, -1);
        assertImportColumn("duringTime", true, -1);
    }

    @Test
    void importDtoDefinesDictionaryEnumAndIgnoredFields() throws NoSuchFieldException {
        assertThat(TeacherStatisticsImportDTO.class.getDeclaredField("teacherCommonType").getAnnotation(DictFormat.class).dictType())
                .isEqualTo("account_status");
        ExcelEnumFormat enumFormat = TeacherStatisticsImportDTO.class.getDeclaredField("hasInvalid").getAnnotation(ExcelEnumFormat.class);
        assertThat(enumFormat.preset()).isEqualTo(ExcelEnumPreset.YES_NO);
        assertIgnored("checkTime");
        assertIgnored("lastSyncTime");
        assertIgnored("remark");
    }

    @Test
    void templateDownloadDelegatesToCommonServiceWithNameAndAlias() throws IOException {
        CommonService service = mock(CommonService.class);
        CommonController controller = new CommonController(service);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.fileDownload("teacher-template", "teacher-import", response);

        verify(service).tempDownload("teacher-template", "teacher-import", response);
    }

    @Test
    void templateDownloadSwallowsServiceExceptionToKeepResponseHandlingStable() throws IOException {
        CommonService service = mock(CommonService.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        doThrow(new IOException("download failed")).when(service).tempDownload("missing", "alias", response);
        CommonController controller = new CommonController(service);

        assertThatNoException().isThrownBy(() -> controller.fileDownload("missing", "alias", response));
    }

    private static void assertImportColumn(String fieldName, boolean required, int columnWidth) throws NoSuchFieldException {
        Field field = TeacherStatisticsImportDTO.class.getDeclaredField(fieldName);
        ImportColumn importColumn = field.getAnnotation(ImportColumn.class);
        assertThat(importColumn.required()).isEqualTo(required);
        assertThat(importColumn.columnWidth()).isEqualTo(columnWidth);
    }

    private static void assertIgnored(String fieldName) throws NoSuchFieldException {
        Field field = TeacherStatisticsImportDTO.class.getDeclaredField(fieldName);
        assertThat(field.getAnnotation(ExcelIgnore.class)).isNotNull();
        assertThat(field.getAnnotation(ImportColumn.class)).isNull();
    }
}
