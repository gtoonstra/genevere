package org.gt.connections.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IReader;

import java.io.EOFException;
import java.sql.*;

public class JdbcReader extends JdbcReaderWriter implements IReader {

    private static final Logger logger = LogManager.getLogger();

    private ResultSet rs;
    private int numColumns;

    public void prepareSource() throws GenevereException {
        String sql = this.props.get("sql");
        if (sql == null) {
            logger.error("The sql property is not set in the reader config");
            throw new GenevereException("The sql property is not set in the reader config");
        }

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
