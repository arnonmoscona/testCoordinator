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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by arnon on 9/1/14.
 */
public class TestCoordinatorTest {

    public static final int TEST_TIMEOUT = 250;
    Executor background = null;
    AtomicBoolean trueExpectation = null;

    @Before
    public void setup() {
        background = Executors.newSingleThreadExecutor((r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        trueExpectation = new AtomicBoolean(false);
    }

    private void delayExecution(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBackgroundJob(TestCoordinator coord) {
        background.execute(makeBackgroundJob(coord));
    }

    private Runnable makeBackgroundJob(TestCoordinator coord) {
        return () -> {
            trueExpectation.set(true);
            delayExecution(20);
            coord.finishTest();
        };
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testWaitsToFinish() throws Exception {
        final TestCoordinator coord = new TestCoordinator();

        doBackgroundJob(coord);

        coord.delayTestFinish();
        assertTrue("background task not executed", trueExpectation.get());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testWaitsToFinish_WithTimeout() throws Exception {

        final TestCoordinator coord = new TestCoordinator();

        doBackgroundJob(coord);

        final boolean result = coord.delayTestFinish(1000);  //default is milliseconds
        assertTrue("background task not executed (delayed wait)", trueExpectation.get());
        assertTrue("task should not have timed out", result);
    }

    @Test(timeout = 1000)
    public void testErrorCondition_WithTimeout() throws Exception {

        final TestCoordinator coord = new TestCoordinator();
        boolean result = coord.delayTestFinish(500);
        assertFalse("task should have timed out", result);
    }

    @Test(timeout = 2000)
    public void testErrorCondition_WithTimeoutAndTimeUnit() throws Exception {

        final TestCoordinator coord = new TestCoordinator();
        final boolean result = coord.delayTestFinish(1, TimeUnit.SECONDS);
        assertFalse("task should have timed out", result);
    }

    @Test(timeout = 1000)
    public void testWaitsToFinish_WithTimeoutAndTimeUnit() throws Exception {

        final TestCoordinator coord = new TestCoordinator();
        doBackgroundJob(coord);

        final boolean result = coord.delayTestFinish(1, TimeUnit.SECONDS);
        assertTrue("background task not executed (delayed wait)", trueExpectation.get());
        assertTrue("task should not have timed out", result);
    }

    @Test(timeout = 1000)
    public void testWaitsToFinishCanBeCalledTwice() throws Exception {

        final TestCoordinator coord = new TestCoordinator();

        final Runnable task = makeBackgroundJob(coord);

        background.execute(task);
        coord.delayTestFinish();
        assertTrue("1st task not called!", trueExpectation.get());

        trueExpectation.set(false);
        background.execute(task);
        coord.delayTestFinish();
        assertTrue("2nd task not called!", trueExpectation.get());
    }

    @Test(timeout = 1000)
    public void testErrorCondition_FinishCalledTwice() throws Exception {

        final TestCoordinator coord = new TestCoordinator();
        coord.finishTest();
        // this shows that nothing bad happens if call finish twice
        coord.finishTest();
    }

}
