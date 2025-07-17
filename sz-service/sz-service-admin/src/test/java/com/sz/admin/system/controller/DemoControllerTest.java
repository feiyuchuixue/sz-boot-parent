package com.sz.admin.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sz.admin.system.pojo.dto.demo.DemoCreateDTO;
import com.sz.admin.system.pojo.dto.demo.DemoUpdateDTO;
import com.sz.admin.system.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DemoController测试类
 * 
 * @author sz
 * @since 2025-01-14
 */
@WebMvcTest(DemoController.class)
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoService demoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDemo() throws Exception {
        DemoCreateDTO dto = new DemoCreateDTO();
        dto.setName("测试Demo");
        dto.setDescription("这是一个测试Demo");
        dto.setStatus(1);
        dto.setType("test");
        dto.setSort(1);

        mockMvc.perform(post("/demo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateDemo() throws Exception {
        DemoUpdateDTO dto = new DemoUpdateDTO();
        dto.setId(1L);
        dto.setName("更新后的Demo");
        dto.setDescription("这是一个更新后的Demo");
        dto.setStatus(1);
        dto.setType("test");
        dto.setSort(2);

        mockMvc.perform(put("/demo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDemoById() throws Exception {
        mockMvc.perform(get("/demo/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDemos() throws Exception {
        mockMvc.perform(get("/demo/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchDemos() throws Exception {
        mockMvc.perform(get("/demo/search")
                .param("name", "测试")
                .param("status", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckName() throws Exception {
        mockMvc.perform(get("/demo/check-name")
                .param("name", "测试Demo"))
                .andExpect(status().isOk());
    }
}