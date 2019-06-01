package org.gt.connections.google;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IWriteConverter;
import org.gt.pipeline.IWriter;
import org.gt.pipeline.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StorageWriter extends StorageReaderWriter implements IWriter {

    private static final Logger logger = LogManager.getLogger();

    private BlobInfo blobInfo;
    private IWriteConverter converter;
    private String bucket;
    private String blobName;
    private String contentType;
    private String converterClass;

    public void configure(Map<String, String> props) throws GenevereException {
        super.configure(props);

        bucket = props.get("bucket");
        if (bucket == null) {
            logger.error("The bucket property is not set in the writer config");
            throw new GenevereException("The bucket property is not set in the writer config");
        }
        blobName = props.get("blob_name");
        if (blobName == null) {
            logger.error("The blob_name property is not set in the writer config");
            throw new GenevereException("The blob_name property is not set in the writer config");
        }
        contentType = props.get("content_type");
        if (contentType == null) {
            logger.error("The content_type property is not set in the writer config");
            throw new GenevereException("The content_type property is not set in the writer config");
        }

        converterClass = Utils.getSafeString(props, "converter", "org.gt.conversions.read.ToJsonConverter");
    }

    public void prepareTarget() throws GenevereException {
        converter = Utils.getWriteConverter(converterClass);
        converter.open(tmpFile.getAbsolutePath());

        BlobId blobId = BlobId.of(bucket, blobName);
        blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
    }

    public void write(Object[] data) throws GenevereException {
        converter.next(data);
        totalRecords++;
    }

    public void terminate() throws GenevereException {
        try {
            converter.terminate();

            byte[] buffer = new byte[1024];
            WriteChannel writer = storage.writer(blobInfo);

            Path path = Paths.get(tmpFile.getAbsolutePath());
            InputStream input = Files.newInputStream(path);
            int limit;
            while ((limit = input.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
            }
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
            throw new GenevereException("An error occurred terminating the connection", ex);
        }
    }
}
