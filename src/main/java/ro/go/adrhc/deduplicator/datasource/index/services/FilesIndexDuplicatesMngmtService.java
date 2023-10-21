package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataCopies;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReader;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.util.Assert;
import ro.go.adrhc.util.io.SimpleDirectory;
import ro.go.adrhc.util.pair.UnaryPair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static ro.go.adrhc.util.EnumUtils.toNamesSet;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;
import static ro.go.adrhc.util.text.StringUtils.concat;

@RequiredArgsConstructor
@Slf4j
public class FilesIndexDuplicatesMngmtService {
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final SimpleDirectory duplicatesDirectory;
	private final Supplier<Path> filesPathSupplier;

	public FileMetadataDuplicates find() throws IOException {
		return indexReaderTemplate.useReader(this::doFind);
	}

	public boolean removeDups() throws IOException {
		FileMetadataDuplicates duplicates = find();
		Path filesPath = filesPathSupplier.get();
		List<List<UnaryPair<Path>>> origDupAndTargetPaths =
				duplicates.stream().map(copies -> origDupAndTargetPaths(filesPath, copies)).toList();
		logOrigDupAndTargetPaths(origDupAndTargetPaths);
		copyOrigMoveDups(origDupAndTargetPaths);
		return !origDupAndTargetPaths.isEmpty();
	}

	private void logOrigDupAndTargetPaths(List<List<UnaryPair<Path>>> origDupAndTargetPaths) {
		if (origDupAndTargetPaths.isEmpty()) {
			return;
		}
		log.debug("\n{}", concat("\n\n", origDupAndTargetPaths.stream()
				.map(set -> concat(set.stream()
						.map(p -> "%s -> %s".formatted(p.key(), p.value()))))));
	}

	private void copyOrigMoveDups(List<List<UnaryPair<Path>>> origDupAndTargetPaths) throws IOException {
		for (List<UnaryPair<Path>> originalAndDuplicates : origDupAndTargetPaths) {
			UnaryPair<Path> original = originalAndDuplicates.get(0);
			duplicatesDirectory.cp(original.key(), original.value());
			for (int i = 1; i < originalAndDuplicates.size(); i++) {
				UnaryPair<Path> duplicate = originalAndDuplicates.get(i);
				duplicatesDirectory.mv(duplicate.key(), duplicate.value());
			}
		}
	}

	private List<UnaryPair<Path>> origDupAndTargetPaths(Path filesPath, FileMetadataCopies copies) {
		Optional<String> optionalOrigFilenameNoExt = filenameNoExt(copies.getOriginal().getPath());
		Assert.isTrue(optionalOrigFilenameNoExt.isPresent(), "Original filename stripped from extension should exist!");
		String origFilenameNoExt = optionalOrigFilenameNoExt.get();
		Path relativeToFiles = filesPath.relativize(copies.getOriginalPath());
		List<UnaryPair<Path>> pathPairs = new ArrayList<>();
		pathPairs.add(new UnaryPair<>(copies.getOriginalPath(), relativeToFiles));
		for (Path dup : copies.getDuplicatePaths()) {
			Path filesRelativeCopy = filesPath.relativize(dup);
			String prefixedCopyFilename = origFilenameNoExt + " - " + filesRelativeCopy.getFileName().toString();
			Path prefixedFilesRelativeCopy = filesRelativeCopy.resolveSibling(prefixedCopyFilename);
			pathPairs.add(new UnaryPair<>(dup, prefixedFilesRelativeCopy));
		}
		return pathPairs;
	}

	private FileMetadataDuplicates doFind(DocumentIndexReader indexReader) {
		Stream<FileMetadata> metadataStream = indexReader
				.getAll(toNamesSet(IndexFieldType.class))
				.map(toFileMetadataConverter::convert);
		return FileMetadataDuplicates.of(metadataStream);
	}
}
