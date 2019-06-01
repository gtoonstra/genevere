package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseReaderWriter implements IStopWatch {

    private static final Logger logger = LogManager.getLogger();

    private Stopwatch stopWatch = new Stopwatch();

    protected long totalRecords = 0;

    public long getTotalRecords() {
        return totalRecords;
    }

    public void start() {
        stopWatch.reset();
    }

    public void lap() {
        stopWatch.lapTime();
    }

    public void stop() {
        stopWatch.stop();
    }

    public long getTotalTime() {
        return Stopwatch.toMilliseconds(stopWatch.getTotalTime());
    }

    public long getAverageLapTime() {
        return Stopwatch.toMilliseconds(stopWatch.getAverageLapTime());
    }

    public long getMaxLapTime() {
        return Stopwatch.toMilliseconds(stopWatch.getMaxLapTime());
    }

    public long getMinLapTime() {
        return Stopwatch.toMilliseconds(stopWatch.getMinLapTime());
    }
}
