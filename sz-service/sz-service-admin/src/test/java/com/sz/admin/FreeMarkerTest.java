package com.sz.admin;

import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.service.GeneratorTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FreeMarkerTest {

    @Autowired
    private GeneratorTableService generatorTableService;

    @Autowired
    private GeneratorProperties generatorProperties;

    @Test
    public void tes2() {
        System.out.println(generatorProperties.getPath().getApi());
        System.out.println(generatorProperties.getPath().getWeb());
    }

}