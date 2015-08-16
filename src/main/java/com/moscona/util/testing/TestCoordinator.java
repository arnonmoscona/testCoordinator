/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
