package org.gt.pipeline;

import org.gt.GenevereException;

import java.io.EOFException;

public interface IWriteConverter {
    void open(String fileName) throws GenevereException;
    void next(Object[] data) throws GenevereException;
    void terminate() throws GenevereException;
}
