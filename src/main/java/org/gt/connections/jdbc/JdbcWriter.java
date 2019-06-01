package org.gt.connections.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IWriter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcWriter extends JdbcReaderWriter implements IWriter {

    private static final Logger logger = LogManager.getLogger();

    private PreparedStatement ps;

    public void prepareTarget() throws GenevereException {
        String sql = this.props.get("sql");
        if (sql == null) {
            logger.error("The sql property is not set in the writer config");
            throw new GenevereException("The sql property is not set in the writer config");
        }

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

                numTotal += numBatchWritten;
                numCommitted += numBatchWritten;

                if (numCommitted >= commitSize) {
                    this.conn.commit();
                    logger.info("Committed " + numTotal + " records.");
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
            numTotal += numBatchWritten;
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
