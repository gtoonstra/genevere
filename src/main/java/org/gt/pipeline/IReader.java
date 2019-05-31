package org.gt.pipeline;

import org.gt.GenevereException;

import java.io.EOFException;
import java.util.Map;

public interface IReader {
    void setConfiguration(Map<String, String> props);
    void connect(String username, String password) throws GenevereException;
    void prepareSource() throws GenevereException;
    int getNumColumns();
    void read(Object[] row) throws GenevereException, EOFException;
    void init_reader(Map<String, String> config) throws GenevereException;
}
