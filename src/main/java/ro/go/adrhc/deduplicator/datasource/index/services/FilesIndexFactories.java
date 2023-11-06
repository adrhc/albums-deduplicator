package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.index.domain.queries.FieldQueries;
import ro.go.adrhc.persistence.lucene.index.search.IndexSearchService;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexCreateService;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexFactories;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexRemoveService;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexUpdateService;
import ro.go.adrhc.persistence.lucene.typedindex.core.TypedIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.typedindex.core.docds.DocumentsDataSourceFactory;
import ro.go.adrhc.persistence.lucene.typedindex.domain.seach.QuerySearchResult;
import ro.go.adrhc.persistence.lucene.typedindex.restore.DocumentsIndexRestoreService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.persistence.lucene.typedindex.search.TypedSearchByIdService;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	public static final FieldQueries ID_QUERIES =
			FieldQueries.create(FileMetadataFieldType.filePath);

	private final TypedIndexFactories<String,
			FileMetadata, FileMetadataFieldType> typedIndexFactories;
	private final FileMetadataProvider fileMetadataProvider;
	private final SimpleDirectory duplicatesDirectory;

	public FilesIndexDedupService createDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				TypedIndexReaderTemplate.create(FileMetadata.class, indexPath),
				duplicatesDirectory, filesRoot);
	}

	/**
	 * BestMatchingStrategy: Stream::findFirst
	 * QuerySearchResultFilter: it -> true (aka no filter)
	 */
	public IndexSearchService<QuerySearchResult<FileMetadata>>
	createSearchService(Path indexPath) {
		return typedIndexFactories
				.createTypedIndexSearchService(Stream::findFirst, it -> true, indexPath);
	}

	public TypedSearchByIdService<String, FileMetadata> createSearchByIdService(Path indexPath) {
		return typedIndexFactories.createSearchByIdService(indexPath);
	}

	public TypedIndexCreateService<FileMetadata> createCreateService(Path indexPath) {
		return typedIndexFactories.createTypedIndexCreateService(indexPath);
	}

	public TypedIndexUpdateService<FileMetadata> createUpdateService(Path indexPath) {
		return typedIndexFactories.createTypedIndexUpdateService(indexPath);
	}

	public TypedIndexRemoveService<String> createIndexRemoveService(Path indexPath) {
		return typedIndexFactories.createIndexRemoveService(indexPath);
	}

	public DocumentsIndexRestoreService<String, FileMetadata> createIndexRestoreService(Path indexPath) {
		return typedIndexFactories.createDocumentsIndexRestoreService(indexPath);
	}

	public IndexDataSource<String, Document> createIndexDataSource() {
		return DocumentsDataSourceFactory.create(typedIndexFactories.getAnalyzer(),
				FileMetadataFieldType.class, fileMetadataProvider);
	}
}
