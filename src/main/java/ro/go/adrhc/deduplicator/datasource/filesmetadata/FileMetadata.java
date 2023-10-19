package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.nio.file.Path;
import java.time.Instant;

@Getter
@RequiredArgsConstructor
@ToString
public class FileMetadata {
	private final Path path;
	private final Instant lastModified;
	private final long size;
	private final String fileHash;
}
