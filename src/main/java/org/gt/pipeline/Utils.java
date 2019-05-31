package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;

import java.util.Map;

public class Utils {

    private static final Logger logger = LogManager.getLogger();

    public static long getSafeLong(Map<String, String> props, String key, long defaultValue) throws GenevereException {
        String configElem = props.get(key);
        if (configElem == null) {
            logger.warn("The " + key + " was not set in the config. Applying " + defaultValue);
            return defaultValue;
        } else {
            try {
                return new Long(configElem).longValue();
            } catch (NumberFormatException ex) {
                logger.error("The " + key + " is not a valid long.");
                throw new GenevereException("The " + key + " is not a valid long.", ex);
            }
        }
    }

    public static int getSafeInt(Map<String, String> props, String key, int defaultValue) throws GenevereException {
        String configElem = props.get(key);
        if (configElem == null) {
            logger.warn("The " + key + " was not set in the config. Applying " + defaultValue);
            return defaultValue;
        } else {
            try {
                return new Integer(configElem).intValue();
            } catch (NumberFormatException ex) {
                logger.error("The " + key + " is not a valid long.");
                throw new GenevereException("The " + key + " is not a valid long.", ex);
            }
        }
    }
}
