package ro.go.adrhc.deduplicator.stub;

import lombok.experimental.UtilityClass;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;

import java.nio.file.Path;
import java.time.Instant;

@UtilityClass
public class FileMetadataGenerator {
    public static final String FILENAME_NO_EXT = "Elvis Presley - Are You";
    public static final String FILENAME = FILENAME_NO_EXT + ".wma";
    public static final int SIZE = 1024;
    public static final Instant LAST_MODIFIED = Instant.parse("2011-12-03T10:15:30Z");
    public static final String FILE_HASH = "fileHash";

    public static FileMetadata create() {
        return new FileMetadata(Path.of(FILENAME), FILENAME_NO_EXT, LAST_MODIFIED, FILE_HASH, SIZE);
    }
}
