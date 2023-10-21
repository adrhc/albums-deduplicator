package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.AppDirectoryFactories;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.MetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexDuplicatesMngmtService;
import ro.go.adrhc.deduplicator.datasource.index.services.FullFilesIndexUpdateService;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.FSTypedIndex.createFSIndex;
import static ro.go.adrhc.util.fn.SneakyFunctionUtils.toSneakyFunction;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	private final AppPaths appPaths;
	private final FilesIndexProperties indexProperties;
	private final LuceneTokenizer luceneTokenizer;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataToDocumentConverter toDocumentConverter;
	private final AppDirectoryFactories appDirectoryFactories;
	private final MetadataProvider<Path, FileMetadata> metadataProvider;

	public FilesIndex<Path, FileMetadata> createFilesIndex(Path indexPath) {
		return new FilesIndex<>(metadataProvider, createFSTypedIndex(indexPath));
	}

	public FilesIndexDuplicatesMngmtService createFilesIndexDuplicatesSearchService(Path indexPath) {
		return new FilesIndexDuplicatesMngmtService(toFileMetadataConverter,
				createDocumentIndexReaderTemplate(indexPath),
				appDirectoryFactories.duplicatesDirectory(),
				appPaths::getFilesPath);
	}

	public FullFilesIndexUpdateService<Path, FileMetadata> createFullFilesIndexUpdateService(Path indexPath) {
		return new FullFilesIndexUpdateService<>(IndexFieldType.filePath.name(),
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
