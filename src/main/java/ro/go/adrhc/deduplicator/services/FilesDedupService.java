package ro.go.adrhc.deduplicator.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.util.Assert;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static ro.go.adrhc.util.fn.BiFunctionUtils.curry;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;
import static ro.go.adrhc.util.text.StringUtils.concat;

@RequiredArgsConstructor
@Slf4j
public class FilesDedupService {
	private final SimpleDirectory duplicatesDirectory;
	private final Path filesRoot;
	private final IndexRepository<Path, FileMetadata> indexRepository;

	public FileMetadataCopiesCollection find() throws IOException {
		return indexRepository.reduce(FileMetadataCopiesCollection::of);
	}

	public boolean removeDups() throws IOException {
		FileMetadataCopiesCollection duplicates = find();
		if (duplicates.isEmpty()) {
			return false;
		}
		List<OriginalAndDupBackups> originalAndDupBackups =
				duplicates.stream().map(this::originalAndDupBackups).toList();
		log.debug("\n{}", concat("\n\n", originalAndDupBackups));
		copyOrigMoveDups(originalAndDupBackups);
		return true;
	}

	private void copyOrigMoveDups(List<OriginalAndDupBackups> originalAndDupBackups) throws IOException {
		for (OriginalAndDupBackups backups : originalAndDupBackups) {
			duplicatesDirectory.cp(backups.getOriginalPath(), backups.getOriginalBackupPath());
			for (PathAndBackup backup : backups) {
				duplicatesDirectory.mv(backup.getPath(), backup.getBackupPath());
			}
		}
	}

	private OriginalAndDupBackups originalAndDupBackups(FileMetadataCopies copies) {
		PathAndBackup origBackup = createOrigBackup(copies.getOriginal());
		OriginalAndDupBackups backups = OriginalAndDupBackups.of(origBackup);
		String origFilenameNoExt = origFilenameNoExt(copies);
		copies.pathsStream()
				.map(curry(this::createDupBackup, origFilenameNoExt))
				.forEach(backups::add);
		return backups;
	}

	private String origFilenameNoExt(FileMetadataCopies copies) {
		Optional<String> optionalOrigFilenameNoExt = filenameNoExt(copies.getOriginalPath());
		Assert.isTrue(optionalOrigFilenameNoExt.isPresent(),
				"Original filename stripped from extension should exist!");
		return optionalOrigFilenameNoExt.get();
	}

	private PathAndBackup createOrigBackup(FileMetadata original) {
		Path originalPath = original.path();
		Path origRelativeToFilesRoot = filesRoot.relativize(originalPath);
		return new PathAndBackup(originalPath, origRelativeToFilesRoot);
	}

	private PathAndBackup createDupBackup(String origFilenameNoExt, Path path) {
		Path filesRootRelativeCopy = filesRoot.relativize(path);
		String origPrefixedCopyFilename = origFilenameNoExt + " - " + filesRootRelativeCopy.getFileName().toString();
		Path filesRootRelativeOrigPrefixedCopy = filesRootRelativeCopy.resolveSibling(origPrefixedCopyFilename);
		return new PathAndBackup(path, filesRootRelativeOrigPrefixedCopy);
	}
}
