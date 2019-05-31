package org.gt.pipeline;

import static java.lang.System.nanoTime;

public class Stopwatch {
    protected long totalTime;
    protected long maxLapTime;
    protected long minLapTime = Long.MAX_VALUE;
    protected long lapsCount;
    protected long lastThreshold;

    /**
     * Human readable time in seconds
     *
     * @param nanoTime
     * @return time in seconds
     */
    public static final long toMilliseconds(long nanoTime) { return new Double(nanoTime / 1000000.0).longValue(); }

    public long getAverageLapTime() {
        if (lapsCount == 0)
            return 0;
        return totalTime / lapsCount;
    }

    public long getMaxLapTime() {
        return maxLapTime;
    }

    public long getMinLapTime() {
        return minLapTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Returns last lap time, process statistic.
     */
    public long lapTime() {
        return processLapTime();
    }

    private long processLapTime() {
        if (lastThreshold == 0)
            throw new IllegalStateException("Stopwatch is stopped.");
        final long now = nanoTime();
        final long lapTime = now - lastThreshold;
        lapsCount++;
        totalTime += lapTime;
        if (lapTime > maxLapTime)
            maxLapTime = lapTime;
        if (lapTime < minLapTime)
            minLapTime = lapTime;
        lastThreshold = nanoTime();
        return lapTime;
    }

    /**
     * Resets statistic and starts.
     */
    public void reset() {
        totalTime = 0;
        maxLapTime = 0;
        minLapTime = Long.MAX_VALUE;
        lapsCount = 0;
        start();
    }

    /**
     * Starts time watching.
     */
    public void start() {
        lastThreshold = nanoTime();
    }

    /**
     * Suspends time watching, returns last lap time.
     */
    public long stop() {
        final long lapTime = processLapTime();
        lastThreshold = 0;
        return lapTime;
    }

}
