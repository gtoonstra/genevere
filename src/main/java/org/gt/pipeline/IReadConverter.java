package org.gt.pipeline;

import org.gt.GenevereException;

import java.io.EOFException;

public interface IReadConverter {
    int getNumColumns();
    void open(String fileName) throws GenevereException;
    Object next() throws GenevereException, EOFException;
}
