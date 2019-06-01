package org.gt.pipeline;

import org.gt.GenevereException;

import java.util.Map;

public interface IWriter extends IStopWatch {
    void configure(Map<String, String> config) throws GenevereException;
    void connect(String username, String password) throws GenevereException;
    void prepareTarget() throws GenevereException;
    void write(Object[] data) throws GenevereException;
    void terminate() throws GenevereException;
    long getTotalRecords();
}
