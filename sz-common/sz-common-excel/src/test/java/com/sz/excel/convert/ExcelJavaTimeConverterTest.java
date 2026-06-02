package com.sz.excel.convert;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelJavaTimeConverterTest {

    @Test
    void localDateShouldExportAsStandardDateString() {
        CustomLocalDateStringConvert converter = new CustomLocalDateStringConvert();

        assertThat(converter.supportJavaTypeKey()).isEqualTo(LocalDate.class);
        assertThat(converter.convertToExcelData(LocalDate.of(2026, 5, 30), null, null).getStringValue()).isEqualTo("2026-05-30");
    }

    @Test
    void localDateTimeShouldExportAsStandardDateTimeString() {
        CustomLocalDateTimeStringConvert converter = new CustomLocalDateTimeStringConvert();

        assertThat(converter.supportJavaTypeKey()).isEqualTo(LocalDateTime.class);
        assertThat(converter.convertToExcelData(LocalDateTime.of(2026, 5, 30, 8, 19, 5), null, null).getStringValue())
                .isEqualTo("2026-05-30 08:19:05");
    }

    @Test
    void localTimeShouldExportAsStandardTimeString() {
        CustomLocalTimeStringConvert converter = new CustomLocalTimeStringConvert();

        assertThat(converter.supportJavaTypeKey()).isEqualTo(LocalTime.class);
        assertThat(converter.convertToExcelData(LocalTime.of(8, 19, 5), null, null).getStringValue()).isEqualTo("08:19:05");
    }
}
