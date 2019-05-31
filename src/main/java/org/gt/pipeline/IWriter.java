package org.gt.pipeline;

import org.gt.GenevereException;

import java.util.Map;

public interface IWriter extends IStopWatch {
    void setConfiguration(Map<String, String> props);
    void connect(String username, String password) throws GenevereException;
    void setNumCols(int numCols);
    void prepareTarget() throws GenevereException;
    void write(Object[] row) throws GenevereException;
    void terminate() throws GenevereException;
    long getTotalRecords();
    void init_writer(Map<String, String> config) throws GenevereException;
}
