package ro.go.adrhc.deduplicator.datasource.index.services.dedup;

import lombok.Getter;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
		return original.getPath();
	}

	public Set<Path> getDuplicatePaths() {
		return duplicates.stream().map(FileMetadata::getPath).collect(Collectors.toSet());
	}

	public boolean hasDuplicates() {
		return !duplicates.isEmpty();
	}

	public int duplicatesCount() {
		return duplicates.size();
	}

	public String toString() {
		if (original == null) {
			return null;
		} else if (duplicates.isEmpty()) {
			return """
					Original: %s
					No duplicates for %s"""
					.formatted(original.getPath(), original.getFileHash());
		} else {
			return """
					Original: %s
					%d duplicates for %s:
					%s"""
					.formatted(original.getPath(), duplicates.size(), original.getFileHash(),
							concat(FileMetadata::getPath, duplicates));
		}
	}
}
