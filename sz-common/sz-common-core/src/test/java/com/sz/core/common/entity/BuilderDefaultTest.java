package com.sz.core.common.entity;

import com.sz.core.common.dict.DictTypeVO;
import com.sz.core.common.enums.CommonResponseEnum;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BuilderDefaultTest {

    @Test
    void accessLogBuilderShouldKeepTypeDefaults() {
        assertThat(AccessRequestLog.builder().build().getType()).isEqualTo("request");
        assertThat(AccessResponseLog.builder().build().getType()).isEqualTo("response");
    }

    @Test
    void dictBuilderShouldKeepDynamicDefaultFalse() {
        assertThat(DictTypeVO.builder().build().isDynamic()).isFalse();
        assertThat(DictVO.builder().build().isDynamic()).isFalse();
    }

    @Test
    void responseEnumShouldUseNonDeprecatedSpringStatusNames() {
        assertThat(CommonResponseEnum.FILE_UPLOAD_SIZE_ERROR.httpStatus()).isEqualTo(HttpStatus.CONTENT_TOO_LARGE);
        assertThat(CommonResponseEnum.EXCEL_IMPORT_ERROR.httpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
