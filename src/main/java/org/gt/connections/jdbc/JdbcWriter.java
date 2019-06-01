package org.gt.connections.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IWriter;
import org.gt.pipeline.Utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class JdbcWriter extends JdbcReaderWriter implements IWriter {

    private static final Logger logger = LogManager.getLogger();

    private PreparedStatement ps;
    private long batchSize;
    private long commitSize;
    private long numBatchWritten;
    private long numCommitted;
    private String sql;

    public void configure(Map<String, String> props) throws GenevereException {
        super.configure(props);

        if (props == null || props.isEmpty()) {
            logger.error("No writer configuration detected");
            throw new GenevereException("No writer configuration detected");
        }
        batchSize = Utils.getSafeLong(props, "batch_size", Long.MAX_VALUE);
        commitSize = Utils.getSafeLong(props, "commit_size", Long.MAX_VALUE);
        logger.info("Using a batch size of: " + batchSize + " and commit size of: " + commitSize);

        sql = props.get("sql");
        if (sql == null) {
            logger.error("The sql property is not set in the writer config");
            throw new GenevereException("The sql property is not set in the writer config");
        }

        if (commitSize < batchSize) {
            logger.error("Commit size must be greater or equal to batch size.");
            throw new GenevereException("Commit size must be greater or equal to batch size.");
        }

        numBatchWritten = 0;
        numCommitted = 0;
    }

    public void prepareTarget() throws GenevereException {
        try {
            ps = this.conn.prepareStatement(sql);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new GenevereException("An error occurred executing the statement", ex);
        }
    }

    public void write(Object[] row) throws GenevereException {
        try {
            for (int i = 0; i < row.length; i++) {
                ps.setObject(i + 1, row[i]);
            }
            ps.addBatch();
            numBatchWritten++;

            if (numBatchWritten >= batchSize) {
                ps.executeBatch();
                ps.clearBatch();

                // call base class to update stopwatch
                this.lap();

                totalRecords += numBatchWritten;
                numCommitted += numBatchWritten;

                if (numCommitted >= commitSize) {
                    this.conn.commit();
                    logger.info("Committed " + totalRecords + " records.");
                    numCommitted = 0;
                }

                numBatchWritten = 0;
            }
        } catch (SQLException ex) {
            logger.error(ex);
            try {
                this.conn.rollback();
            } catch (SQLException e) {
                logger.error(ex);
            }
            throw new GenevereException("An exception occurred writing to the database.", ex);
        }
    }

    public void terminate() throws GenevereException {
        try {
            totalRecords += numBatchWritten;
            numBatchWritten = 0;
            ps.executeBatch();
            ps.clearBatch();
            this.conn.commit();
            this.ps.close();
            this.conn.close();
        } catch (SQLException ex) {
            logger.info(ex);
            try {
                this.conn.rollback();
            } catch (SQLException e) {
                logger.error(ex);
            }
            throw new GenevereException("An error occurred terminating the operation.");
        }
    }
}
