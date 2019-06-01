package org.gt.pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.Configuration;
import org.gt.GenevereException;

import java.util.ArrayList;
import java.util.List;

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

        List<ITransform> transforms = new ArrayList<ITransform>();
        List<TransformConfig> transformConfigs = config.getTransforms();
        if (transformConfigs != null) {
            for (TransformConfig transformConfig : transformConfigs) {
                ITransform transform = transformConfig.getTransform();
                transform.init_transform(transformConfig.getConfig());
                transforms.add(transform);
            }
        }
        pipeline.setTransforms(transforms);

        return pipeline;
    }
}
