package com.sz.core.util;

import com.sz.core.common.service.Treeable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TreeUtilsTest {

    @Test
    void buildTreeConnectsDescendantsByStringCompatibleIds() {
        TestNode root = new TestNode(0L, -1L);
        TestNode dept = new TestNode(1L, 0L);
        TestNode userGroup = new TestNode(2L, "1");

        List<TestNode> tree = TreeUtils.buildTree(List.of(dept, userGroup), root);

        assertThat(tree).containsExactly(root);
        assertThat(root.getChildren()).containsExactly(dept);
        assertThat(dept.getChildren()).containsExactly(userGroup);
    }

    @Test
    void buildTreeCanExcludeTargetNodeAndItsDescendants() {
        TestNode root = new TestNode(0L, -1L);
        TestNode kept = new TestNode(1L, 0L);
        TestNode excluded = new TestNode(2L, 0L);
        TestNode excludedChild = new TestNode(3L, 2L);

        TreeUtils.buildTree(List.of(kept, excluded, excludedChild), root, 2L);

        assertThat(root.getChildren()).containsExactly(kept);
    }

    @Test
    void buildTreeWithoutCustomRootFindsTopLevelNodesFromStartPid() {
        TestNode root = new TestNode(1L, 0L);
        TestNode child = new TestNode(2L, 1L);
        TestNode unrelated = new TestNode(3L, 99L);

        List<TestNode> tree = TreeUtils.buildTree(List.of(root, child, unrelated), 0L);

        assertThat(tree).containsExactly(root);
        assertThat(root.getChildren()).containsExactly(child);
    }

    public static class TestNode implements Treeable<TestNode> {

        private Object id;

        private Object pid;

        private List<TestNode> children;

        TestNode() {
        }

        TestNode(Object id, Object pid) {
            this.id = id;
            this.pid = pid;
        }

        @Override
        public Object getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public Object getPid() {
            return pid;
        }

        public void setPid(Long pid) {
            this.pid = pid;
        }

        @Override
        public Long getDeep() {
            return null;
        }

        @Override
        public Long getSort() {
            return null;
        }

        @Override
        public List<TestNode> getChildren() {
            return children;
        }

        @Override
        public void setChildren(List<TestNode> children) {
            this.children = children;
        }
    }
}
