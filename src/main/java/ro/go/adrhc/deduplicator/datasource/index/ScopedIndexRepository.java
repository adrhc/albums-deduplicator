package ro.go.adrhc.deduplicator.datasource.index;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.config.apppaths.IndexPathObserver;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepositoryFactory;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexContext;
import ro.go.adrhc.persistence.lucene.typedindex.factories.TypedIndexFactoriesParamsFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScopedIndexRepository implements IndexPathObserver, Closeable {
	private final AppPaths appPaths;
	private final FilesIndexProperties indexProperties;
	private final PathExistsFilter pathExistsFilter;
	private IndexObjects indexObjects;

	public IndexRepository<Path, FileMetadata> getIndexRepository() throws IOException {
		initialize();
		return indexObjects.indexRepository();
	}

	@SneakyThrows
	@Override
	public void indexPathChanged() {
		close();
		initialize();
	}

	protected void initialize() throws IOException {
		if (indexObjects != null) {
			return;
		}
		indexObjects = IndexObjects.create(this);
		log.info("\nopened {}", indexObjects.getIndexPath());
	}

	@PreDestroy
	protected void preDestroy() throws IOException {
		close();
	}

	@Override
	public void close() throws IOException {
		if (indexObjects != null) {
			indexObjects.close();
			indexObjects = null;
		}
	}

	private record IndexObjects(TypedIndexContext<FileMetadata> params,
			IndexRepository<Path, FileMetadata> indexRepository) implements Closeable {
		public static IndexObjects create(ScopedIndexRepository scopedIndexRepository) throws IOException {
			TypedIndexContext<FileMetadata> params = createParams(scopedIndexRepository);
			return new IndexObjects(params, IndexRepositoryFactory.create(params));
		}

		public Path getIndexPath() {
			return params.getIndexPath();
		}

		@Override
		public void close() throws IOException {
			params.close();
		}

		private static TypedIndexContext<FileMetadata>
		createParams(ScopedIndexRepository scopedIndexRepository) throws IOException {
			return TypedIndexFactoriesParamsFactory.create(
					FileMetadata.class, FileMetadataFieldType.class,
					scopedIndexRepository.indexProperties.getTokenizer(),
					scopedIndexRepository.pathExistsFilter,
					scopedIndexRepository.indexProperties.getSearch().getMaxResultsPerSearch(),
					scopedIndexRepository.appPaths.getIndexPath());
		}
	}
}
