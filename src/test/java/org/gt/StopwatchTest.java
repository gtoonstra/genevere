package org.gt;

import org.gt.pipeline.Stopwatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Genevere console app
 */
public class StopwatchTest
{
    @Test
    public void testStopWatch()
    {
        Stopwatch watch = new Stopwatch();
        watch.reset();

        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                watch.lapTime();
            }
        } catch ( InterruptedException e ) {
            fail("fail");
        }
        watch.stop();

        assertEquals(Math.abs((watch.getAverageLapTime() / 1000000.0) - 1000) < 200, true );
    }
}
