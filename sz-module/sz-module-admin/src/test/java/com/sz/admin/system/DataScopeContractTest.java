package com.sz.admin.system;

import com.sz.db.permission.DataScopeConstant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataScopeContractTest {

    @Test
    void dataScopeCodesMatchBusinessDictionaryContract() {
        assertThat(DataScopeConstant.ALL).isEqualTo("1006001");
        assertThat(DataScopeConstant.DEPT_AND_BELOW).isEqualTo("1006002");
        assertThat(DataScopeConstant.DEPT_ONLY).isEqualTo("1006003");
        assertThat(DataScopeConstant.SELF_ONLY).isEqualTo("1006004");
        assertThat(DataScopeConstant.CUSTOM).isEqualTo("1006005");
    }

    @Test
    void nonCustomDataScopesKeepPermissionPriorityOrder() {
        assertThat(List.of(DataScopeConstant.ALL, DataScopeConstant.DEPT_AND_BELOW, DataScopeConstant.DEPT_ONLY, DataScopeConstant.SELF_ONLY))
                .containsExactly("1006001", "1006002", "1006003", "1006004");
    }
}
