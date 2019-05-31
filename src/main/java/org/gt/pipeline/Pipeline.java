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

public class Pipeline {

    private static final Logger logger = LogManager.getLogger();

    private IReader reader;
    private IWriter writer;

    public void setReader(IReader reader) {
        this.reader = reader;
    }

    public void setWriter(IWriter writer) {
        this.writer = writer;
    }

    public void execute() throws GenevereException {
        logger.info("Executing the pipeline");

        reader.prepareSource();
        int numCols = reader.getNumColumns();
        writer.setNumCols(numCols);
        writer.prepareTarget();

        Object[] row = new Object[numCols];

        try {
            writer.start();
            while (true) {
                reader.read(row);
                writer.write(row);
            }
        } catch (EOFException ex) {
            // do nothing.
        }

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
