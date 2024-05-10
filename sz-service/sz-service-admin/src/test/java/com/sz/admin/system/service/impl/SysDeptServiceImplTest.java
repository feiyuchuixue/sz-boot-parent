package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.vo.sysdept.DeptTreeVO;
import com.sz.admin.system.service.SysDeptService;
import com.sz.core.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @ClassName SysDeptServiceImplTest
 * @Author sz
 * @Date 2024/3/22 13:43
 * @Version 1.0
 */

@SpringBootTest
class SysDeptServiceImplTest {

    @Autowired
    private SysDeptService sysDeptService;

    @Test
    void getDeepTreeVOS() {
        List<DeptTreeVO> tree = sysDeptService.getDeptTree(null, true, false);
        System.out.println("树形:" + JsonUtils.toJsonString(tree));
    }
}