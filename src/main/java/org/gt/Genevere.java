package org.gt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.pipeline.Pipeline;
import org.gt.pipeline.PipelineBuilder;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Main class for Genevere
 */
public class Genevere {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("You must specify a configuration file as the only argument");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            Configuration config = mapper.readValue(reader, Configuration.class);
            config.configure();

            logger.info("Using configuration:" );
            logger.info(ReflectionToStringBuilder.toString(config, ToStringStyle.MULTI_LINE_STYLE));

            Pipeline pipeline = PipelineBuilder.build(config);
            pipeline.execute();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }
}
