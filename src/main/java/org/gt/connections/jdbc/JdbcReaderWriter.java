package org.gt.connections.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.BaseReaderWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public abstract class JdbcReaderWriter extends BaseReaderWriter {

    private static final Logger logger = LogManager.getLogger();

    protected Connection conn;
    private String jdbcUrl;

    public void configure(Map<String, String> props) throws GenevereException {
        jdbcUrl = props.get("jdbc_url");
    }

    public void terminate() throws GenevereException {
        try {
            this.conn.close();
        } catch (SQLException ex) {
            logger.error(ex);
            throw new GenevereException("Could not terminate connection", ex);
        }
    }

    public void connect(String username, String password) throws GenevereException {
        try {
            this.conn = DriverManager.getConnection(jdbcUrl, username, password);
            this.conn.setAutoCommit(false);
            logger.info("Connected to " + jdbcUrl);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new GenevereException("Could not connect to: " + jdbcUrl, ex);
        }
    }
}
