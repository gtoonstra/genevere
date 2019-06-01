package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;

import java.util.Map;

public class TransformConfig {

    private static final Logger logger = LogManager.getLogger();

    private String java_class;
    private Map<String, String> config;

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getJava_class() {
        return java_class;
    }

    public void setJava_class(String java_class) {
        this.java_class = java_class;
    }

    public ITransform getTransform() throws GenevereException {
        try {
            return (ITransform)Utils.createClass(java_class);
        } catch (ClassCastException e) {
            logger.error(e);
            throw new GenevereException("Class " + java_class + " could not be cast to the ITransform interface", e);
        }
    }
}
