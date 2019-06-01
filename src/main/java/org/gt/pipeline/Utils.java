package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;

import java.util.Map;

public class Utils {

    private static final Logger logger = LogManager.getLogger();

    public static long getSafeLong(Map<String, String> props, String key, long defaultValue) throws GenevereException {
        if (props.containsKey(key)) {
            String configElem = props.get(key);
            try {
                return new Long(configElem).longValue();
            } catch (NumberFormatException ex) {
                logger.error("The " + key + " is not a valid long.");
                throw new GenevereException("The " + key + " is not a valid long.", ex);
            }
        }
        logger.warn("The " + key + " was not set in the config. Applying " + defaultValue);
        return defaultValue;
    }

    public static int getSafeInt(Map<String, String> props, String key, int defaultValue) throws GenevereException {
        if (props.containsKey(key)) {
            String configElem = props.get(key);
            try {
                return new Integer(configElem).intValue();
            } catch (NumberFormatException ex) {
                logger.error("The " + key + " is not a valid long.");
                throw new GenevereException("The " + key + " is not a valid long.", ex);
            }
        }
        logger.warn("The " + key + " was not set in the config. Applying " + defaultValue);
        return defaultValue;
    }

    public static String getSafeString(Map<String, String> props, String key, String defaultValue) throws GenevereException {
        if (props.containsKey(key)) {
            return props.get(key);
        }
        logger.warn("The " + key + " was not set in the config. Applying " + defaultValue);
        return defaultValue;
    }

    public static Object createClass(String javaClass) throws GenevereException {
        try {
            Class<?> clazz = Class.forName(javaClass);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            logger.error(e);
            throw new GenevereException("Class " + javaClass + " not found", e);
        } catch (InstantiationException e) {
            logger.error(e);
            throw new GenevereException("Class " + javaClass + " could not be instantiated", e);
        } catch (IllegalAccessException e) {
            logger.error(e);
            throw new GenevereException("Class " + javaClass + " has illegal access in constructor", e);
        }
    }

    public static IReadConverter getReadConverter(String javaClass) throws GenevereException {
        try {
            return (IReadConverter)Utils.createClass(javaClass);
        } catch (ClassCastException e) {
            logger.error(e);
            throw new GenevereException("Class " + javaClass + " could not be cast to the IReadConverter interface", e);
        }
    }

    public static IWriteConverter getWriteConverter(String javaClass) throws GenevereException {
        try {
            return (IWriteConverter)Utils.createClass(javaClass);
        } catch (ClassCastException e) {
            logger.error(e);
            throw new GenevereException("Class " + javaClass + " could not be cast to the IWriteConverter interface", e);
        }
    }
}
