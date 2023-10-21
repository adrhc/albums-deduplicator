package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.AppDirectoryFactories;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataToDocumentConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.deduplicator.datasource.index.services.update.FilesIndexFullUpdateService;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;
import ro.go.adrhc.persistence.lucene.domain.MetadataProvider;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.services.IndexCreateService;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.FSTypedIndex.createFSIndex;
import static ro.go.adrhc.util.fn.SneakyFunctionUtils.toSneakyFunction;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	private final FilesIndexProperties indexProperties;
	private final LuceneTokenizer luceneTokenizer;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataToDocumentConverter toDocumentConverter;
	private final AppDirectoryFactories appDirectoryFactories;
	private final MetadataProvider<Path, FileMetadata> metadataProvider;

	public IndexCreateService<Path, FileMetadata> createFilesIndexCreateService(Path indexPath) {
		return new IndexCreateService<>(metadataProvider, createFSTypedIndex(indexPath));
	}

	public FilesIndexDedupService createFilesIndexDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				createFilesIndexReaderTemplate(indexPath),
				appDirectoryFactories.duplicatesDirectory(), filesRoot);
	}

	public FilesIndexReaderTemplate createFilesIndexReaderTemplate(Path indexPath) {
		return new FilesIndexReaderTemplate(toFileMetadataConverter,
				createDocumentIndexReaderTemplate(indexPath));
	}

	public FilesIndexFullUpdateService<Path, FileMetadata> createFilesIndexFullUpdateService(Path indexPath) {
		return new FilesIndexFullUpdateService<>(IndexFieldType.filePath.name(),
				metadataProvider, Path::of, createDocumentIndexReaderTemplate(indexPath),
				createFSTypedIndex(indexPath));
	}

	private DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}

	private FSTypedIndex<FileMetadata> createFSTypedIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(),
				toSneakyFunction(toDocumentConverter::convert), indexPath);
	}
}
