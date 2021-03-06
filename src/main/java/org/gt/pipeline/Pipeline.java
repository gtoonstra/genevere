package org.gt.pipeline;

import com.sun.management.OperatingSystemMXBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;

import java.io.EOFException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Pipeline {

    private static final Logger logger = LogManager.getLogger();

    private IReader reader;
    private IWriter writer;
    private List<ITransform> transforms;
    private long timeout;

    public void setReader(IReader reader) {
        this.reader = reader;
    }

    public void setWriter(IWriter writer) {
        this.writer = writer;
    }

    public void setTransforms(List<ITransform> transforms) { this.transforms = transforms; }

    public void setTimeout(long timeout) {
        logger.info("Using a timeout of " + timeout + " seconds.");
        this.timeout = timeout;
    }

    public void execute() throws GenevereException {
        logger.info("Executing the pipeline");

        reader.prepareSource();
        int numCols = reader.getNumColumns();
        writer.prepareTarget();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logger.warn("Process timed out!");
                try {
                    reader.terminate();
                } catch (GenevereException ex) {
                    logger.error(ex);
                }
                try {
                    writer.terminate();
                } catch (GenevereException ex) {
                    logger.error(ex);
                }
                System.exit(-2);
            };
        };
        timer.schedule(task,timeout * 1000);

        Object[] row = new Object[numCols];

        if (transforms.size() == 0) {
            try {
                writer.start();
                while (true) {
                    reader.read(row);
                    writer.write(row);
                }
            } catch (EOFException ex) {
                // do nothing.
            }
        } else {
            try {
                writer.start();
                while (true) {
                    row = new Object[numCols];
                    reader.read(row);
                    for (ITransform transform: transforms) {
                        row = transform.transform(row);
                    }
                    writer.write(row);
                }
            } catch (EOFException ex) {
                // do nothing.
            }
        }

        timer.cancel();

        writer.stop();
        writer.terminate();

        long recordCount = writer.getTotalRecords();
        long totalTime = writer.getTotalTime();

        logger.info("Finished processing " + recordCount + " records.");

        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        logger.info("Recent cpu load for this process: " + bean.getProcessCpuLoad());

        logger.info("Total time: " + totalTime + "ms");
        logger.info("Min lap time: " + writer.getMinLapTime() + "ms");
        logger.info("Max lap time: " + writer.getMaxLapTime() + "ms");
        logger.info("Average lap time: " + writer.getAverageLapTime() + "ms");

        logger.info("Processed " + ((recordCount * 1000) / totalTime) + " recs/sec");

        // Place this code just before the end of the program
        try {
            List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean pool : pools) {
                MemoryUsage peak = pool.getPeakUsage();
                logger.info(String.format("Peak %s memory used: %,d", pool.getName(),peak.getUsed()));
                logger.info(String.format("Peak %s memory reserved: %,d", pool.getName(), peak.getCommitted()));
            }
        } catch (Throwable t) {
            logger.error(t);
        }

        logger.info("Cpu time utilized: " + new Double(bean.getProcessCpuTime() / 1000000.0).longValue() + "ms");
    }
}
