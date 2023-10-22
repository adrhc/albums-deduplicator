package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadatadocs.FileMetadataDocumentsProvider;
import ro.go.adrhc.deduplicator.datasource.index.LuceneFactories;
import ro.go.adrhc.deduplicator.datasource.index.core.FilesIndexFactories;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.index.IndexCreateService;
import ro.go.adrhc.persistence.lucene.index.update.IndexFullUpdateService;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FilesIndexServicesFactories {
	private final LuceneFactories luceneFactories;
	private final FilesIndexFactories filesIndexFactories;
	private final FileMetadataDocumentsProvider fileMetadataDocumentsProvider;
	private final SimpleDirectory duplicatesDirectory;

	public FilesIndexDedupService createFilesIndexDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				filesIndexFactories.createFilesIndexReaderTemplate(indexPath),
				duplicatesDirectory, filesRoot);
	}

	public IndexFullUpdateService createFilesIndexFullUpdateService(Path indexPath) {
		return new IndexFullUpdateService(IndexFieldType.filePath.name(),
				fileMetadataDocumentsProvider,
				luceneFactories.createDocumentIndexReaderTemplate(indexPath),
				filesIndexFactories.createFilesIndex(indexPath));
	}

	public IndexCreateService createFilesIndexCreateService(Path indexPath) {
		return new IndexCreateService(fileMetadataDocumentsProvider,
				filesIndexFactories.createFilesIndex(indexPath));
	}
}
