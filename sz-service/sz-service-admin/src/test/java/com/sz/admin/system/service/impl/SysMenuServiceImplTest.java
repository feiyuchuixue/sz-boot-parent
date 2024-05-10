package com.sz.admin.system.service.impl;

import com.sz.admin.system.pojo.dto.sysmenu.SysMenuQueryDTO;
import com.sz.admin.system.pojo.vo.sysmenu.SysMenuVO;
import com.sz.admin.system.service.SysMenuService;
import com.sz.core.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SysMenuServiceImplTest {

    @Autowired
    private SysMenuService sysMenuService;

    @Test
    void menuList() {
        SysMenuQueryDTO dto = new SysMenuQueryDTO();
        List<SysMenuVO> sysMenuVOS = sysMenuService.menuList(dto);
        System.out.println("sysMenuVOS ==" + JsonUtils.toJsonString(sysMenuVOS));
    }
}