package com.sz.core.common.dict;

import com.sz.core.common.entity.DictVO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DictLoaderFactoryTest {

    @Test
    void dynamicLoaderUsesFullDynamicTypeCode() {
        TestDynamicLoader loader = new TestDynamicLoader("user_options", "用户信息");
        DictLoaderFactory factory = new DictLoaderFactory(List.of(loader));

        assertThat(loader.getDynamicPrefix()).isEqualTo("dynamic_");
        assertThat(loader.getDynamicTypeCode()).isEqualTo("dynamic_user_options");
        assertThat(factory.getDictByType("dynamic_user_options")).hasSize(1).extracting(DictVO::getSysDictTypeCode).containsExactly("dynamic_user_options");
        assertThat(factory.getDictByType("user_options")).isEmpty();
    }

    @Test
    void dynamicLoaderRejectsBlankAndDuplicateTypeCode() {
        assertThatThrownBy(() -> new DictLoaderFactory(List.of(new TestDynamicLoader("", "空类型")))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("typeCode");

        assertThatThrownBy(() -> new DictLoaderFactory(List.of(new TestDynamicLoader("dept_options", "部门A"), new TestDynamicLoader("dept_options", "部门B"))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("dynamic_dept_options");
    }

    @Test
    void staticLoaderIsUsedWhenNoDynamicLoaderMatches() {
        DictLoader staticLoader = () -> Map.of("account_status", List.of(DictVO.builder().sysDictTypeCode("account_status").codeName("正常").build()));
        DictLoaderFactory factory = new DictLoaderFactory(List.of(staticLoader, new TestDynamicLoader("role_options", "角色信息")));

        assertThat(factory.loadStaticDict()).containsOnlyKeys("account_status");
        assertThat(factory.getDictByType("account_status")).hasSize(1).extracting(DictVO::getCodeName).containsExactly("正常");
        assertThat(factory.getDictByType("dynamic_role_options")).hasSize(1).extracting(DictVO::getSysDictTypeCode).containsExactly("dynamic_role_options");
        assertThat(factory.getDictByType("")).isEmpty();
    }

    private record TestDynamicLoader(String typeCode, String typeName) implements DynamicDictLoader {

        @Override
        public String getTypeCode() {
            return typeCode;
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public Map<String, List<DictVO>> loadDict() {
            return Map.of(getDynamicTypeCode(), List.of(DictVO.builder().sysDictTypeCode(getDynamicTypeCode()).codeName(typeName).isDynamic(true).build()));
        }
    }
}
