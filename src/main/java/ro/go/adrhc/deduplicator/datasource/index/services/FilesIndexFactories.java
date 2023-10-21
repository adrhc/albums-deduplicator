package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataDocumentsProvider;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.services.IndexCreateService;
import ro.go.adrhc.persistence.lucene.services.update.FilesIndexFullUpdateService;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.LuceneIndexFactories.createFSIndex;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
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

	public FilesIndexReaderTemplate createFilesIndexReaderTemplate(Path indexPath) {
		return new FilesIndexReaderTemplate(toFileMetadataConverter,
				createDocumentIndexReaderTemplate(indexPath));
	}

	public FilesIndexFullUpdateService createFilesIndexFullUpdateService(Path indexPath) {
		return new FilesIndexFullUpdateService(IndexFieldType.filePath.name(),
				fileMetadataDocumentsProvider,
				createDocumentIndexReaderTemplate(indexPath),
				createFSLuceneIndex(indexPath));
	}

	public IndexCreateService createFilesIndexCreateService(Path indexPath) {
		return new IndexCreateService(fileMetadataDocumentsProvider, createFSLuceneIndex(indexPath));
	}

	private DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}

	private FSLuceneIndex createFSLuceneIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(), indexPath);
	}
}
