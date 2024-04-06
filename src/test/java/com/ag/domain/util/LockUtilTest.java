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
    private String key = "test_key";
    private int expiredMs = (int) LockUtil.WAIT_TRY_LOCK_MS;

    @Test
    public void testLock_10_10() throws InterruptedException {
        executeMultiService(10, 10);
    }

    @Test
    public void testLock_10_1000() throws InterruptedException {
        executeMultiService(10, 1000);
    }

    @Test
    public void testLock_100_1000() throws InterruptedException {
        executeMultiService(100, 1000);
    }
//TODO: thread should test one after one
    @Test
    public void testLock_500_10() throws InterruptedException {
        executeMultiService(500, 10);
    }

    private void executeMultiService(int threadNumber, int runtimeMs) throws InterruptedException {
        // Arrange
        AtomicInteger finishCount = new AtomicInteger();
        Supplier<AtomicInteger> supplier = createSupplier(finishCount, runtimeMs);

        // Create a fixed-size thread pool to simulate concurrent access
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        // Use CountDownLatch to wait for all threads to finish
        CountDownLatch latch = new CountDownLatch(threadNumber);

        // Act
        startThreads(threadNumber, key, executorService, supplier, latch);
        waitAndShutdownThreads(latch, executorService);

        // Assert
        assertTrue(executorService.isTerminated());
        assertEquals(0, latch.getCount());
        int expect = calculateFinishThreads(threadNumber, runtimeMs, expiredMs);
        assertTrue(finishCount.intValue() <= expect,
                String.format("Finish Threads should be min(finishCount of threads %s, (expired time %s / runtime of each thread %s))",
                        threadNumber, expiredMs, runtimeMs));
        log.warn("Threads number={}, finished={}, expect={}", threadNumber, finishCount, expect);
    }

    private int calculateFinishThreads(int threadNumber, int runtimeMs, int expiredMs) {
        int capability = expiredMs / runtimeMs;
        return Math.min(threadNumber, capability);
    }

    private Supplier<AtomicInteger> createSupplier(AtomicInteger finishCount, long runtimeMs) {
        return () -> {
            try {
                Thread.sleep(runtimeMs);
                finishCount.incrementAndGet();
                log.debug("===> Finish count=" + finishCount);
                return finishCount;
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        };
    }

    private <T> void startThreads(int numThreads, String key, ExecutorService executorService, Supplier<T> supplier, CountDownLatch latch) {
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    LockUtil.lock(key, supplier);
                } catch (LockNotGotException e) {
                } catch (InterruptedException e) {
                    log.error("Error thread", e);
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

