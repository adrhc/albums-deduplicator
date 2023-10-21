package ro.go.adrhc.deduplicator.datasource.index.services.dedup;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileMetadataCopiesCollection {
	private final Map<String, FileMetadataCopies> duplicates;

	public static FileMetadataCopiesCollection create() {
		return new FileMetadataCopiesCollection(new HashMap<>());
	}

	public static FileMetadataCopiesCollection of(Stream<FileMetadata> metadataStream) {
		FileMetadataCopiesCollection duplicates = create();
		metadataStream.forEach(duplicates::add);
		return duplicates;
	}

	public void add(FileMetadata metadata) {
		FileMetadataCopies metadataCopies = duplicates
				.computeIfAbsent(metadata.getFileHash(), hash -> new FileMetadataCopies());
		metadataCopies.add(metadata);
	}

	public Stream<FileMetadataCopies> stream() {
		return duplicates.values().stream()
				.filter(FileMetadataCopies::hasDuplicates)
				.sorted(Comparator.comparing(FileMetadataCopies::duplicatesCount));
	}

	public long count() {
		return stream().count();
	}

	public String toString() {
		long count = count();
		if (count == 0) {
			return "There are no duplicates!";
		} else {
			return """
					%s
									
					Found %d files containing duplicates!"""
					.formatted(stream()
							.map(FileMetadataCopies::toString)
							.collect(Collectors.joining("\n\n")), count);
		}
	}
}
