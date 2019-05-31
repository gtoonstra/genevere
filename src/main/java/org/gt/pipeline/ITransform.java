package org.gt.pipeline;

import org.gt.GenevereException;

import java.util.Map;

public interface ITransform {
    void init_transform(Map<String, String> props) throws GenevereException;
    Object[] transform(Object[] object) throws GenevereException;
}
