package org.gt.pipeline;

public interface IStopWatch {
    void start();
    void lap();
    void stop();

    long getTotalTime();
    long getAverageLapTime();
    long getMaxLapTime();
    long getMinLapTime();
}
