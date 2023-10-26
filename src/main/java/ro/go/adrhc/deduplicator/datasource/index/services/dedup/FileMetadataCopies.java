package ro.go.adrhc.deduplicator.datasource.index.services.dedup;

import lombok.Getter;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static ro.go.adrhc.util.text.StringUtils.concat;

@Getter
public class FileMetadataCopies {
	private final Set<FileMetadata> duplicates = new HashSet<>();
	private FileMetadata original;

	public void add(FileMetadata metadata) {
		if (original == null) {
			original = metadata;
		} else if (metadata.isBefore(original)) {
			duplicates.add(original);
			original = metadata;
		} else {
			duplicates.add(metadata);
		}
	}

	public Path getOriginalPath() {
		return original.path();
	}

	public Stream<Path> pathsStream() {
		return duplicates.stream().map(FileMetadata::path);
	}

	public boolean hasDuplicates() {
		return !duplicates.isEmpty();
	}

	public int count() {
		return duplicates.size();
	}

	public String toString() {
		if (original == null) {
			return null;
		} else if (duplicates.isEmpty()) {
			return """
					Original: %s
					No duplicates for %s"""
					.formatted(original.path(), original.fileHash());
		} else {
			return """
					Original: %s
					%d duplicates for %s:
					%s"""
					.formatted(original.path(), duplicates.size(), original.fileHash(),
							concat(FileMetadata::path, duplicates));
		}
	}
}
