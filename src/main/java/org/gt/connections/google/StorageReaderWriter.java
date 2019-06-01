package org.gt.connections.google;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.BaseReaderWriter;
import org.gt.pipeline.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class StorageReaderWriter extends BaseReaderWriter {
    private static final Logger logger = LogManager.getLogger();

    protected Storage storage;
    protected File tmpFile;
    private String prefix;
    private String suffix;

    public void configure(Map<String, String> props) throws GenevereException {
        prefix = Utils.getSafeString(props, "prefix", "gene");
        suffix = Utils.getSafeString(props, "suffix", "vere");
    }

    public void connect(String username, String password) throws GenevereException {
        storage = StorageOptions.getDefaultInstance().getService();

        try {
            tmpFile = File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not create a temporary file", ex);
        }

        tmpFile.deleteOnExit();
    }

    public void terminate() throws GenevereException {
        // not much that can be done here.
    }
}
