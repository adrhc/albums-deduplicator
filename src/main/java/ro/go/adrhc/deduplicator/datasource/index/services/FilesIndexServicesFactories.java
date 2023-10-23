package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.core.FilesIndexFactories;
import ro.go.adrhc.deduplicator.datasource.index.core.LuceneFactories;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.fsindex.FSIndexCreateService;
import ro.go.adrhc.persistence.lucene.fsindex.FSIndexUpdateService;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;
import ro.go.adrhc.persistence.lucene.index.spi.DocumentsDatasource;
import ro.go.adrhc.persistence.lucene.index.update.DSIndexRestoreService;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FilesIndexServicesFactories {
	private final LuceneFactories luceneFactories;
	private final FilesIndexFactories filesIndexFactories;
	private final DocumentsDatasource documentsDatasource;
	private final SimpleDirectory duplicatesDirectory;
	private final LuceneTokenizer luceneTokenizer;

	public FilesIndexDedupService createFilesIndexDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				filesIndexFactories.createFilesIndexReaderTemplate(indexPath),
				duplicatesDirectory, filesRoot);
	}

	public DSIndexRestoreService createFilesIndexFullUpdateService(Path indexPath) {
		return DSIndexRestoreService.create(IndexFieldType.filePath, documentsDatasource,
				luceneFactories.createDocumentIndexReaderTemplate(indexPath),
				createFilesIndex(indexPath));
	}

	public FSIndexCreateService createFilesFSIndexCreateService(Path indexPath) {
		return new FSIndexCreateService(documentsDatasource,
				createFilesIndex(indexPath));
	}

	private FSIndexUpdateService createFilesIndex(Path indexPath) {
		return FSIndexUpdateService.create(
				IndexFieldType.filePath, luceneTokenizer.analyzer(), indexPath);
	}
}
