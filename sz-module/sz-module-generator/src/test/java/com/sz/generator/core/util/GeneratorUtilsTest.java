package com.sz.generator.core.util;

import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorUtilsTest {

    @Test
    void uploadResourceColumnsShouldNotBeEnabledForExcelImportOrExportByDefault() throws Exception {
        GeneratorTableColumn column = new GeneratorTableColumn();
        column.setColumnType("varchar(512)");
        column.setJavaType("String");
        column.setIsPk("0");

        Method method = GeneratorUtils.class.getDeclaredMethod("setColumnAttributes", String.class, GeneratorTableColumn.class);
        method.setAccessible(true);
        method.invoke(null, "product_image", column);

        assertThat(column.getHtmlType()).isEqualTo(GeneratorConstants.HTML_IMAGE_UPLOAD);
        assertThat(column.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
        assertThat(column.getIsImport()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
        assertThat(column.getIsExport()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
    }
}
