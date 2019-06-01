package org.gt.connections.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IReader;
import org.gt.pipeline.Utils;

import java.io.EOFException;
import java.sql.*;
import java.util.Map;

public class JdbcReader extends JdbcReaderWriter implements IReader {

    private static final Logger logger = LogManager.getLogger();

    private ResultSet rs;
    private int numColumns;
    private int fetchSize;
    private String sql;

    public void configure(Map<String, String> props) throws GenevereException {
        super.configure(props);

        if (props == null || props.isEmpty()) {
            logger.error("No reader configuration detected");
            throw new GenevereException("No reader configuration detected");
        }
        fetchSize = Utils.getSafeInt(props, "batch_size", 100);
        logger.info("Using a fetch size of: " + fetchSize);

        sql = props.get("sql");
        if (sql == null) {
            logger.error("The sql property is not set in the reader config");
            throw new GenevereException("The sql property is not set in the reader config");
        }
    }

    public void prepareSource() throws GenevereException {
        try {
            Statement stmt = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.FETCH_FORWARD);
            stmt.setFetchSize(fetchSize);
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            numColumns = rsmd.getColumnCount();
        } catch (SQLException ex) {
            logger.error(ex);
            throw new GenevereException("An error occurred executing the query", ex);
        }
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void read(Object[] row) throws GenevereException, EOFException {
        try {
            if (rs.next()) {
                for (int i = 0; i < numColumns; i++) {
                    row[i] = rs.getObject(i+1);
                }
            } else {
                throw new EOFException("Reached end of recordset");
            }
        } catch (SQLException ex) {
            logger.error(ex);
            throw new GenevereException("An SQL exception occurred while paging the result set", ex);
        }
    }
}
