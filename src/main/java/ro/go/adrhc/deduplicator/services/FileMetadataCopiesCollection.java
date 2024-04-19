package ro.go.adrhc.deduplicator.services;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;

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

    public static FileMetadataCopiesCollection of(Stream<FileMetadata> metadata) {
        FileMetadataCopiesCollection duplicates = create();
        metadata.forEach(duplicates::add);
        return duplicates;
    }

    public void add(FileMetadata metadata) {
        FileMetadataCopies metadataCopies = duplicates
                .computeIfAbsent(metadata.fileHash(), hash -> new FileMetadataCopies());
        metadataCopies.add(metadata);
    }

    public Stream<FileMetadataCopies> stream() {
        return notSortedStream().sorted(Comparator.comparing(FileMetadataCopies::count));
    }

    private Stream<FileMetadataCopies> notSortedStream() {
        return duplicates.values().stream()
                .filter(FileMetadataCopies::hasDuplicates);
    }

    public long count() {
        return notSortedStream().count();
    }

    public boolean isEmpty() {
        return duplicates.values().stream().noneMatch(FileMetadataCopies::hasDuplicates);
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
