package org.gt.conversions.read;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IReadConverter;

import java.io.*;

public class FromJsonConverter implements IReadConverter {

    private static final Logger logger = LogManager.getLogger();

    private ObjectMapper mapper = new ObjectMapper();
    private BufferedReader reader;

    public int getNumColumns() {
        return 1;
    }

    public void open(String fileName) throws GenevereException {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            logger.error(ex);
            throw new GenevereException("Could not find temp file", ex);
        }
    }

    public Object next() throws GenevereException, EOFException {
        String nextLine = null;
        try {
            nextLine = reader.readLine();
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not read string from file", ex);
        }
        if (nextLine == null) {
            throw new EOFException("End of file");
        }
        try {
            return mapper.readTree(nextLine);
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("Could not convert string: " + nextLine, ex);
        }
    }
}
