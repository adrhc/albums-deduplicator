package ro.go.adrhc.deduplicator.datasource.index.services;

import com.rainerhahnekamp.sneakythrow.functional.SneakyFunction;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.fsindex.FSIndexCreateService;
import ro.go.adrhc.persistence.lucene.fsindex.FSIndexUpdateService;
import ro.go.adrhc.persistence.lucene.index.core.docds.datasource.DocumentsDataSource;
import ro.go.adrhc.persistence.lucene.index.core.docds.rawidserde.RawIdSerdeFactory;
import ro.go.adrhc.persistence.lucene.index.restore.DSIndexRestoreService;
import ro.go.adrhc.persistence.lucene.index.search.IndexSearchService;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexFactories;
import ro.go.adrhc.persistence.lucene.typedindex.search.TypedSearchResult;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.index.search.SearchedToQueryConverterFactory.ofSneaky;
import static ro.go.adrhc.persistence.lucene.typedindex.core.DocsDataSourceFactory.createTypedDs;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	private final TypedIndexFactories<FileMetadata> typedIndexFactories;
	private final FileMetadataProvider fileMetadataProvider;
	private final SimpleDirectory duplicatesDirectory;

	public FilesIndexDedupService createDedupService(Path indexPath, Path filesRoot) {
		return new FilesIndexDedupService(
				typedIndexFactories.createTypedIndexReaderTemplate(indexPath),
				duplicatesDirectory, filesRoot);
	}

	public DSIndexRestoreService createIndexRestoreService(Path indexPath) {
		return DSIndexRestoreService.create(FileMetadataFieldType.filePath, createDocsDs(),
				typedIndexFactories.createDocumentIndexReaderTemplate(indexPath),
				createUpdateService(indexPath));
	}

	public IndexSearchService<String, TypedSearchResult<String, FileMetadata>>
	createSearchService(
			SneakyFunction<String, Query, QueryNodeException> searchedToQueryConverter,
			Path indexPath) {
		return typedIndexFactories.createTypedFSIndexSearchService(
				ofSneaky(searchedToQueryConverter), indexPath);
	}

	public FSIndexCreateService createCreateService(Path indexPath) {
		return typedIndexFactories.createFSIndexCreateService(createDocsDs(), indexPath);
	}

	public FSIndexUpdateService createUpdateService(Path indexPath) {
		return typedIndexFactories.createFSIndexUpdateService(FileMetadataFieldType.filePath, indexPath);
	}

	public DocumentsDataSource createDocsDs() {
		return createTypedDs(typedIndexFactories.getAnalyzer(),
				FileMetadataFieldType.class, fileMetadataProvider,
				RawIdSerdeFactory.of(Path::of, Path::toString));
	}
}
