package org.gt.connections.google;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gt.GenevereException;
import org.gt.pipeline.IReadConverter;
import org.gt.pipeline.IReader;
import org.gt.pipeline.Utils;

import java.io.EOFException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class StorageReader extends StorageReaderWriter implements IReader {

    private static final Logger logger = LogManager.getLogger();

    private IReadConverter converter;
    private String bucket;
    private String blobName;
    private String converterClass;
    private int bufferSize;

    public void configure(Map<String, String> props) throws GenevereException {
        super.configure(props);

        bucket = props.get("bucket");
        if (bucket == null) {
            logger.error("The bucket property is not set in the reader config");
            throw new GenevereException("The bucket property is not set in the reader config");
        }
        blobName = props.get("blob_name");
        if (blobName == null) {
            logger.error("The blob_name property is not set in the reader config");
            throw new GenevereException("The blob_name property is not set in the reader config");
        }

        converterClass = Utils.getSafeString(props, "converter", "org.gt.conversions.read.FromJsonConverter");

        int bufferSize = Utils.getSafeInt(props, "buffer_size", 65536);
        logger.info("Using buffer size: " + bufferSize);
    }

    public void prepareSource() throws GenevereException {
        BlobId blobId = BlobId.of(bucket, blobName);
        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            logger.error("Blob " + blobId.getName() + " does not exist in bucket " + blobId.getBucket());
            throw new GenevereException("Blob " + blobId.getName() + " does not exist");
        }

        Path path = Paths.get(tmpFile.getAbsolutePath());
        blob.downloadTo(path);

        converter = Utils.getReadConverter(converterClass);
        converter.open(tmpFile.getAbsolutePath());
    }

    public int getNumColumns() {
        return converter.getNumColumns();
    }

    public void read(Object[] row) throws GenevereException, EOFException {
        row[0] = converter.next();
    }
}
