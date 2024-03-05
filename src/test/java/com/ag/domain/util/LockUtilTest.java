package com.ag.domain.util;

import com.ag.domain.exception.LockNotGotException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class LockUtilTest {

    @Test
    public void test_lock_if_all_thread_get_lock() throws InterruptedException {
        // arrange
        String key = "test_key";
        AtomicInteger number = new AtomicInteger();
        Supplier<AtomicInteger> supplier = createSupplier(number, 10);
        int numThreads = 10;

        // Create a fixed-size thread pool to simulate concurrent access
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Use CountDownLatch to wait for all threads to finish
        CountDownLatch latch = new CountDownLatch(numThreads);

        // act
        startThreads(key, executorService, supplier, latch);
        waitAndShutdownThreads(latch, executorService);

        // assert
        assertTrue(executorService.isTerminated());
        assertEquals(0, latch.getCount());
        assertEquals(10, number.intValue()
            ,"All threads should finish cause runtime 10*10 ms < lock expire time 3000ms");
    }


    @Test
    public void test_lock_if_not_all_thread_get_lock() throws InterruptedException {
        // arrange
        String key = "test_key";
        AtomicInteger number = new AtomicInteger();
        Supplier<AtomicInteger> supplier = createSupplier(number, 100);
        int numThreads = 10;

        // Create a fixed-size thread pool to simulate concurrent access
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Use CountDownLatch to wait for all threads to finish
        CountDownLatch latch = new CountDownLatch(numThreads);

        // act
        startThreads(key, executorService, supplier, latch);
        waitAndShutdownThreads(latch, executorService);

        // assert
        assertTrue(executorService.isTerminated());
        assertEquals(0, latch.getCount());
        assertTrue(10 > number.intValue()
            ,"There should some threads not finish cause runtime 1000*10 ms > lock expire time 3000ms");
    }

    private Supplier<AtomicInteger> createSupplier(AtomicInteger number, long runtimeMs) {
        return () -> {
            try {
                Thread.sleep(runtimeMs);
                number.incrementAndGet();
                log.info("===> Thread" + Thread.currentThread() + ", number=" + number);
                return number;
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        };
    }

    private <T> void startThreads(String key, ExecutorService executorService, Supplier<T> supplier, CountDownLatch latch) {
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    LockUtil.lock(key, supplier);
                } catch (LockNotGotException | InterruptedException e) {
                    log.error("Error test thread: " + Thread.currentThread());
                } finally {
                    latch.countDown();
                }
            });
        }
    }

    private void waitAndShutdownThreads(CountDownLatch latch, ExecutorService executorService) throws InterruptedException {
        // Wait for all threads to finish execution
        latch.await();
        log.info("Latch count: " + latch.getCount());

        shutdownThreadPool(executorService);
    }

    private void shutdownThreadPool(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("executorService InterruptedException", e);
            executorService.shutdownNow();
        }
    }
}

