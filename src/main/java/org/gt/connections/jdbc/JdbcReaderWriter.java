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
    protected Map<String, String> props;

    public void setConfiguration(Map<String, String> props) {
        this.props = props;
    }

    public void connect(String username, String password) throws GenevereException {
        String jdbcUrl = props.get("jdbc_url");
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
