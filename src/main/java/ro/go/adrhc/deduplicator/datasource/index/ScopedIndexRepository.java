package ro.go.adrhc.deduplicator.datasource.index;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.config.apppaths.IndexPathObserver;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.factories.TypedIndexContext;
import ro.go.adrhc.persistence.lucene.typedindex.factories.TypedIndexFactoriesParamsFactory;

import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class ScopedIndexRepository implements IndexPathObserver {
	private final AppPaths appPaths;
	private final FilesIndexProperties indexProperties;
	private final PathExistsFilter pathExistsFilter;
	private TypedIndexContext<Path, FileMetadata> params;
	private IndexRepository<Path, FileMetadata> indexRepository;

	public IndexRepository<Path, FileMetadata> getIndexRepository() throws IOException {
		if (params == null) {
			initialize();
		}
		return indexRepository;
	}

	@SneakyThrows
	@Override
	public void indexPathChanged() {
		if (params != null) {
			params.close();
		}
		initialize();
	}

	protected void initialize() throws IOException {
		params = createParams();
		indexRepository = IndexRepository.create(params);
	}

	protected TypedIndexContext<Path, FileMetadata>
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
