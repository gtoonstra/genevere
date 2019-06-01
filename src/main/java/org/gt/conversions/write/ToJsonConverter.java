package org.gt.conversions.write;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IWriteConverter;

import java.io.*;

public class ToJsonConverter implements IWriteConverter {

    private static final Logger logger = LogManager.getLogger();

    private ObjectMapper mapper = new ObjectMapper();
    private BufferedWriter writer;

    public void open(String fileName) throws GenevereException {
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not find temp file", ex);
        }
    }

    public void next(Object[] data) throws GenevereException {
        String line = null;
        try {
            line = mapper.writeValueAsString(data[0]);
            writer.write(line);
            writer.write("\n");
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not convert string: " + line, ex);
        }
    }

    public void terminate() throws GenevereException {
        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not flush / close file.", ex);
        }
    }
}
