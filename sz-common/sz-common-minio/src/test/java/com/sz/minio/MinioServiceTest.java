package com.sz.minio;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MinioServiceTest {

    @Resource
    private MinioService minioService;

    @Test
    void isBucketExists() {
        boolean bucketExists = minioService.isBucketExists();
        System.out.println("bucketExists = " + bucketExists);
        assert bucketExists;
    }

    @Test
    void createBucket() {
    }

    @Test
    void isDirExists() {
        boolean dirExists = minioService.isDirExists("test");
        System.out.println("dirExists = " + dirExists);
        assert !dirExists;
    }

    @Test
    void isObjectExists() {
        boolean exists = minioService.isObjectExists("fuck-you.jpeg");
        System.out.println("exists = " + exists);
        assert !exists;
    }
}