package com.sz.generator.core.smart;

import com.sz.generator.core.GeneratorConstants;
import com.sz.generator.pojo.po.GeneratorTableColumn;
import com.sz.generator.pojo.vo.GeneratorDetailVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorColumnSmartRulesTest {

    @Test
    void shouldInferAutofillFields() {
        GeneratorTableColumn createId = column("create_id", "bigint", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(createId);

        assertThat(createId.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LONG);
        assertThat(createId.getIsAutofill()).isEqualTo(GeneratorConstants.REQUIRE);
        assertThat(createId.getAutofillType()).isEqualTo("FieldFill.INSERT");
        assertThat(createId.getIsInsert()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
        assertThat(createId.getIsEdit()).isEqualTo(GeneratorConstants.NOT_REQUIRE);

        GeneratorTableColumn updateTime = column("update_time", "datetime", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(updateTime);

        assertThat(updateTime.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LOCAL_DATETIME);
        assertThat(updateTime.getIsAutofill()).isEqualTo(GeneratorConstants.REQUIRE);
        assertThat(updateTime.getAutofillType()).isEqualTo("FieldFill.UPDATE");
    }

    @Test
    void shouldInferUploadFieldsAndDisableExcel() {
        GeneratorTableColumn image = column("product_image", "varchar(512)", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(image);

        assertThat(image.getHtmlType()).isEqualTo(GeneratorConstants.HTML_IMAGE_UPLOAD);
        assertThat(image.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
        assertThat(image.getIsImport()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
        assertThat(image.getIsExport()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
        assertThat(image.getIsQuery()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
        assertThat(image.getOptions()).containsEntry("upload-files.accept", "image/*");

        GeneratorTableColumn file = column("contract_file", "varchar(512)", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(file);

        assertThat(file.getHtmlType()).isEqualTo(GeneratorConstants.HTML_FILE_UPLOAD);
        assertThat(file.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LIST_UPLOADRESULT);
    }

    @Test
    void potentialDictionaryFieldWithoutDictShouldStayRunnable() {
        GeneratorTableColumn status = column("order_status", "varchar(32)", "0", "1");
        GeneratorColumnSmartRules.applyImportDefaults(status);

        assertThat(status.getHtmlType()).isEqualTo(GeneratorConstants.HTML_INPUT);
        assertThat(status.getSearchType()).isEqualTo("input");
        assertThat(status.getIsQuery()).isEqualTo(GeneratorConstants.REQUIRE);

        GeneratorDetailVO.Column detailColumn = detailColumn(status);
        List<GeneratorDetailVO.SmartHint> hints = GeneratorColumnSmartRules.buildSmartHints(detailColumn);

        assertThat(hints).anySatisfy(hint -> {
            assertThat(hint.getType()).isEqualTo("warning");
            assertThat(hint.getLabel()).isEqualTo("建议字典");
        });
    }

    @Test
    void potentialDictionaryFieldWithDictShouldUseDictionaryControl() {
        GeneratorTableColumn status = column("order_status", "varchar(32)", "0", "1");
        status.setDictType("order_status");
        GeneratorColumnSmartRules.applyImportDefaults(status);

        assertThat(status.getHtmlType()).isEqualTo(GeneratorConstants.HTML_RADIO);
        assertThat(status.getSearchType()).isEqualTo("select");
        assertThat(status.getDictType()).isEqualTo("order_status");
        assertThat(status.getDictShowWay()).isEqualTo("0");
    }

    @Test
    void dictionaryDisplayWithoutDictShouldBeBlockingHint() {
        GeneratorDetailVO.Column column = detailColumn(column("check_status", "varchar(32)", "0", "1"));
        column.setHtmlType(GeneratorConstants.HTML_SELECT);
        column.setDictType("");

        assertThat(GeneratorColumnSmartRules.buildSmartHints(column)).anySatisfy(hint -> {
            assertThat(hint.getType()).isEqualTo("danger");
            assertThat(hint.getLabel()).isEqualTo("字典缺失");
        });
    }

    @Test
    void shouldSelectHighValueQueryFieldsOnly() {
        GeneratorTableColumn title = column("work_title", "varchar(128)", "0", "1");
        GeneratorColumnSmartRules.applyImportDefaults(title);

        assertThat(title.getIsQuery()).isEqualTo(GeneratorConstants.REQUIRE);
        assertThat(title.getQueryType()).isEqualTo(GeneratorConstants.QUERY_LIKE);

        GeneratorTableColumn remark = column("order_remark", "varchar(512)", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(remark);

        assertThat(remark.getIsQuery()).isEqualTo(GeneratorConstants.NOT_REQUIRE);
    }

    @Test
    void dateTimeFieldsShouldUseBetweenQuery() {
        GeneratorTableColumn orderDate = column("order_date", "date", "0", "0");
        GeneratorColumnSmartRules.applyImportDefaults(orderDate);

        assertThat(orderDate.getJavaType()).isEqualTo(GeneratorConstants.TYPE_LOCAL_DATE);
        assertThat(orderDate.getHtmlType()).isEqualTo(GeneratorConstants.HTML_DATE);
        assertThat(orderDate.getSearchType()).isEqualTo(GeneratorConstants.HTML_DATE_PICKER);
        assertThat(orderDate.getQueryType()).isEqualTo(GeneratorConstants.QUERY_BETWEEN);
        assertThat(orderDate.getIsQuery()).isEqualTo(GeneratorConstants.REQUIRE);
    }

    private static GeneratorTableColumn column(String columnName, String columnType, String isPk, String isRequired) {
        GeneratorTableColumn column = new GeneratorTableColumn();
        column.setColumnName(columnName);
        column.setColumnComment(columnName);
        column.setColumnType(columnType);
        column.setIsPk(isPk);
        column.setIsIncrement("0");
        column.setIsRequired(isRequired);
        return column;
    }

    private static GeneratorDetailVO.Column detailColumn(GeneratorTableColumn source) {
        GeneratorDetailVO.Column column = new GeneratorDetailVO.Column();
        column.setColumnName(source.getColumnName());
        column.setColumnComment(source.getColumnComment());
        column.setColumnType(source.getColumnType());
        column.setJavaType(source.getJavaType());
        column.setHtmlType(source.getHtmlType());
        column.setSearchType(source.getSearchType());
        column.setQueryType(source.getQueryType());
        column.setIsPk(source.getIsPk());
        column.setIsImport(source.getIsImport());
        column.setIsExport(source.getIsExport());
        column.setIsQuery(source.getIsQuery());
        column.setIsAutofill(source.getIsAutofill());
        column.setAutofillType(source.getAutofillType());
        column.setIsLogicDel(source.getIsLogicDel());
        column.setDictType(source.getDictType());
        return column;
    }
}
