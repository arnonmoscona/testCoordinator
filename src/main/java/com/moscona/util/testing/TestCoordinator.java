package com.moscona.util.testing;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by arnon on 9/1/14.
 * A drop-in replacement for JConch TestCoordinator
 */
public class TestCoordinator {
    private CountDownLatch latch = null;

    public TestCoordinator() {
        reset();
    }

    public void finishTest() {
        try {
            latch.countDown();
        }
        finally {
            reset();
        }
    }

    private synchronized void reset() {
        latch = new CountDownLatch(1);
    }

    public void delayTestFinish() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            reset();
        }
    }

    public boolean delayTestFinish(int millis) {
        return delayTestFinish(millis, TimeUnit.MILLISECONDS);
    }

    public boolean delayTestFinish(int i, TimeUnit units) {
        try {
            return latch.await(i, units);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            reset();
        }
    }
}
