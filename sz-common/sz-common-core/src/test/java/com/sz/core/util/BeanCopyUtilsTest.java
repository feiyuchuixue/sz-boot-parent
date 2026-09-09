package com.sz.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeanCopyUtilsTest {

    @Test
    void copyUsesStrictNameAndTypeMatching() {
        Source source = new Source(1L, "admin", 20);

        Target target = BeanCopyUtils.copy(source, Target.class);

        assertThat(target.getId()).isEqualTo(1L);
        assertThat(target.getName()).isEqualTo("admin");
        assertThat(target.getAge()).isNull();
    }

    @Test
    void copyListMapsEachSourceToNewTarget() {
        List<Target> targets = BeanCopyUtils.copyList(List.of(new Source(1L, "admin", 20), new Source(2L, "guest", 18)), Target.class);

        assertThat(targets).extracting(Target::getName).containsExactly("admin", "guest");
        assertThat(targets).extracting(Target::getId).containsExactly(1L, 2L);
    }

    public static class Source {

        private Long id;

        private String name;

        private Integer age;

        Source(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    public static class Target {

        private Long id;

        private String name;

        private String age;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
