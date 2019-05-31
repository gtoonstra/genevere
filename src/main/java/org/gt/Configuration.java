package org.gt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.pipeline.ReaderWriterConfig;

import java.util.Map;

public class Configuration {

    private static final Logger logger = LogManager.getLogger();

    private static final String SOURCE_USERNAME = "SOURCE_USERNAME";
    private static final String SOURCE_PASSWORD = "SOURCE_PASSWORD";
    private static final String TARGET_USERNAME = "TARGET_USERNAME";
    private static final String TARGET_PASSWORD = "TARGET_PASSWORD";

    private ReaderWriterConfig source;
    private ReaderWriterConfig target;

    private String sourceConversion;
    private String targetConversion;
    private Map<String, String> transformations;

    private String sourceUsername;
    private String sourcePassword;
    private String targetUsername;
    private String targetPassword;

    public ReaderWriterConfig getSource() {
        return source;
    }

    public void setSource(ReaderWriterConfig source) {
        this.source = source;
    }

    public ReaderWriterConfig getTarget() {
        return target;
    }

    public void setTarget(ReaderWriterConfig target) {
        this.target = target;
    }

    public String getSourceConversion() {
        return sourceConversion;
    }

    public void setSourceConversion(String sourceConversion) {
        this.sourceConversion = sourceConversion;
    }

    public String getTargetConversion() {
        return targetConversion;
    }

    public void setTargetConversion(String targetConversion) {
        this.targetConversion = targetConversion;
    }

    public Map<String, String> getTransformations() {
        return transformations;
    }

    public void setTransformations(Map<String, String> transformations) {
        this.transformations = transformations;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public String getSourcePassword() {
        return sourcePassword;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getTargetPassword() {
        return targetPassword;
    }

    public void configure() throws GenevereException {
        this.sourceUsername = getEnvVar(SOURCE_USERNAME);
        this.sourcePassword = getEnvVar(SOURCE_PASSWORD);
        this.targetUsername = getEnvVar(TARGET_USERNAME);
        this.targetPassword = getEnvVar(TARGET_PASSWORD);
    }

    private String getEnvVar(String name) throws GenevereException {
        String property = System.getenv(name);
        if (property == null) {
            logger.error("Environment variable " + name + " is not configured");
            throw new GenevereException("Environment variable " + name + " is not configured");
        }
        return property;
    }
}
