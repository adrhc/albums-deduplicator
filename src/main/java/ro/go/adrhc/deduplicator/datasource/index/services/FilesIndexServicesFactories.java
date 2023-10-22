package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadatadocs.FileMetadataDocumentsProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.core.FilesIndexReaderTemplate;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.fsindex.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.index.IndexCreateService;
import ro.go.adrhc.persistence.lucene.index.core.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;
import ro.go.adrhc.persistence.lucene.index.update.IndexFullUpdateService;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.fsindex.LuceneIndexFactories.createFSIndex;

@Component
@RequiredArgsConstructor
public class FilesIndexServicesFactories {
	private final FilesIndexProperties indexProperties;
	private final LuceneTokenizer luceneTokenizer;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataDocumentsProvider fileMetadataDocumentsProvider;
	private final SimpleDirectory duplicatesDirectory;

	public FilesIndexDedupService createFilesIndexDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				createFilesIndexReaderTemplate(indexPath),
				duplicatesDirectory, filesRoot);
	}

	public IndexFullUpdateService createFilesIndexFullUpdateService(Path indexPath) {
		return new IndexFullUpdateService(IndexFieldType.filePath.name(),
				fileMetadataDocumentsProvider,
				createDocumentIndexReaderTemplate(indexPath),
				createFSLuceneIndex(indexPath));
	}

	public IndexCreateService createFilesIndexCreateService(Path indexPath) {
		return new IndexCreateService(fileMetadataDocumentsProvider, createFSLuceneIndex(indexPath));
	}

	private FilesIndexReaderTemplate createFilesIndexReaderTemplate(Path indexPath) {
		return new FilesIndexReaderTemplate(toFileMetadataConverter,
				createDocumentIndexReaderTemplate(indexPath));
	}

	private DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}

	private FSLuceneIndex createFSLuceneIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(), indexPath);
	}
}
