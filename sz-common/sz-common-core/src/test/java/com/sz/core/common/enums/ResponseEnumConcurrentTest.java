package com.sz.core.common.enums;

import com.sz.core.common.exception.common.BusinessExceptionCustomAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public class ResponseEnumConcurrentTest {

    /**
     * CommonResponseEnum.EXISTS.message() 并发问题测试
     */

    @Test
    public void testConcurrentMessage() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        String[] results = new String[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            new Thread(() -> {
                // 每个线程自定义不同的消息
                String customMsg = "msg-" + idx;
                System.out.println("Thread " + idx + " custom message: " + customMsg);
                BusinessExceptionCustomAssert custom = CommonResponseEnum.EXISTS.message(customMsg);
                results[idx] = custom.getMessage();
                latch.countDown();
            }).start();
        }

        latch.await();

        // 检查每个线程拿到的消息是否正确
        for (int i = 0; i < threadCount; i++) {
            System.out.println("Thread " + i + " message: " + results[i]);
            Assertions.assertEquals("msg-" + i, results[i]);
        }
    }
}
