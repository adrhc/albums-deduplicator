package ro.go.adrhc.deduplicator.datasource.index.dedup;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.util.pair.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ro.go.adrhc.util.text.StringUtils.concat;

@RequiredArgsConstructor
public class FileMetadataDuplicates {
	private final Map<String, Set<FileMetadata>> duplicates;

	public static FileMetadataDuplicates create() {
		return new FileMetadataDuplicates(new HashMap<>());
	}

	public void addAll(FileMetadataDuplicates otherDuplicates) {
		this.duplicates.putAll(otherDuplicates.duplicates);
	}

	public void add(FileMetadata metadata) {
		Set<FileMetadata> metadataSet = duplicates
				.computeIfAbsent(metadata.getFileHash(), it -> new HashSet<>());
		metadataSet.add(metadata);
	}

	public Stream<Pair<String, Set<FileMetadata>>> getDuplicates() {
		return duplicates.entrySet().stream()
				.filter(e -> e.getValue().size() > 1)
				.map(Pair::ofMapEntry);
	}

	public long count() {
		return duplicates.entrySet().stream()
				.filter(e -> e.getValue().size() > 1)
				.count();
	}

	public String toString() {
		return """
				%s
								
				Found %d duplicates!"""
				.formatted(getDuplicates()
						.map(this::toString)
						.collect(Collectors.joining("\n\n")), count());
	}

	private String toString(Pair<String, Set<FileMetadata>> duplicate) {
		return """
				Duplicates for %s:
				%s"""
				.formatted(duplicate.key(), concat(duplicate.value().stream().map(FileMetadata::getPath)));
	}
}
