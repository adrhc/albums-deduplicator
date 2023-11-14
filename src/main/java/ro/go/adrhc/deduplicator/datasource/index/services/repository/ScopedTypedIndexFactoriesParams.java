package ro.go.adrhc.deduplicator.datasource.index.services.repository;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.config.apppaths.AppPathsObserver;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataFieldType;
import ro.go.adrhc.deduplicator.datasource.index.services.PathExistsFilter;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.factories.TypedIndexFactoriesParams;
import ro.go.adrhc.persistence.lucene.typedindex.factories.TypedIndexFactoriesParamsFactory;

import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class ScopedTypedIndexFactoriesParams implements AppPathsObserver {
	private final AppPaths appPaths;
	private final FilesIndexProperties indexProperties;
	private final PathExistsFilter pathExistsFilter;
	private TypedIndexFactoriesParams<Path, FileMetadata, FileMetadataFieldType> params;
	private IndexRepository<Path, FileMetadata> indexRepository;

	public IndexRepository<Path, FileMetadata> getIndexRepository() throws IOException {
		if (params == null) {
			initialize();
		}
		return indexRepository;
	}

	@SneakyThrows
	@Override
	public void pathsChanged() {
		if (params != null) {
			params.close();
		}
		initialize();
	}

	protected void initialize() throws IOException {
		params = createParams();
		indexRepository = IndexRepository.create(params);
	}

	protected TypedIndexFactoriesParams<Path, FileMetadata, FileMetadataFieldType>
	createParams() throws IOException {
		return TypedIndexFactoriesParamsFactory.create(FileMetadata.class,
				FileMetadataFieldType.class, indexProperties.getTokenizer(),
				pathExistsFilter, indexProperties.getSearch().getMaxResultsPerSearch(),
				appPaths.getIndexPath());
	}

	@PreDestroy
	protected void preDestroy() throws IOException {
		if (params != null) {
			params.close();
		}
	}
}
