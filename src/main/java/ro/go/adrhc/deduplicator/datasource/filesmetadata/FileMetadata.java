package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ro.go.adrhc.persistence.lucene.typedcore.serde.Identifiable;

import java.nio.file.Path;
import java.time.Instant;

public record FileMetadata(Path path, String fileNameNoExt, Instant
		lastModified, String fileHash, long size) implements Identifiable<Path> {
	public String lastModifiedAsString() {
		return lastModified.toString();
	}

	public boolean isBefore(FileMetadata metadata) {
		return lastModified.isBefore(metadata.lastModified);
	}

	@JsonIgnore
	@Override
	public Path id() {
		return path;
	}
}
