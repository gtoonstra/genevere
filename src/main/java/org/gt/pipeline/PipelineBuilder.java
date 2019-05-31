package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.Configuration;
import org.gt.GenevereException;

public class PipelineBuilder {

    private static final Logger logger = LogManager.getLogger();

    public static Pipeline build(Configuration config) throws GenevereException {
        logger.info("Inspecting configuration and attempting to build pipeline");
        Pipeline pipeline = new Pipeline();

        ReaderWriterConfig source = config.getSource();
        IReader reader = source.getReaderObject();
        reader.init_reader(source.getReader());
        reader.setConfiguration(source.getConfig());
        reader.connect(config.getSourceUsername(), config.getSourcePassword());

        ReaderWriterConfig target = config.getTarget();
        IWriter writer = target.getWriterObject();
        writer.init_writer(target.getWriter());
        writer.setConfiguration(target.getConfig());
        writer.connect(config.getTargetUsername(), config.getTargetPassword());

        pipeline.setReader(reader);
        pipeline.setWriter(writer);

        return pipeline;
    }
}
