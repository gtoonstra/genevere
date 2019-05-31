package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;

import java.util.Map;

public class BaseReaderWriter implements IStopWatch {

    protected int numCols;
    protected long batchSize;
    protected long commitSize;
    protected long numBatchWritten;
    protected long numCommitted;
    protected long numTotal;
    protected int fetchSize;

    private static final Logger logger = LogManager.getLogger();

    private Stopwatch stopWatch = new Stopwatch();

    public void init_writer(Map<String, String> props) throws GenevereException {
        batchSize = Utils.getSafeLong(props, "batch_size", Long.MAX_VALUE);
        commitSize = Utils.getSafeLong(props, "commit_size", Long.MAX_VALUE);
        logger.info("Using a batch size of: " + batchSize + " and commit size of: " + commitSize);

        if (commitSize < batchSize) {
            logger.error("Commit size must be greater or equal to batch size.");
            throw new GenevereException("Commit size must be greater or equal to batch size.");
        }

        numBatchWritten = 0;
        numTotal = 0;
        numCommitted = 0;
    }

    public void init_reader(Map<String, String> props) throws GenevereException {
        fetchSize = Utils.getSafeInt(props, "batch_size", 100);
        logger.info("Using a fetch size of: " + fetchSize);
    }

    public long getBatchSize() {
        return batchSize;
    }

    public long getTotalRecords() {
        return numTotal;
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
