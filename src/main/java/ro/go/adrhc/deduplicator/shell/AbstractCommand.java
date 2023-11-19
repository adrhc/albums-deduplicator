package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableAppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.nio.file.Path;

@RequiredArgsConstructor
public abstract class AbstractCommand {
	protected final ApplicationContext ac;
	protected final ObservableAppPaths appPaths;
	protected final IndexDataSource<Path, FileMetadata> indexDataSource;

	/**
	 * solves 2nd call to count(), otherwise (from 2nd call on):
	 * No qualifying bean of type 'ro.go.adrhc.persistence.lucene.typedindex.IndexRepository
	 * <java.nio.file.Path, ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata>' available
	 */
	protected IndexRepository<Path, FileMetadata> fileMetadataRepository() {
		return ac.getBean(IndexRepository.class);
	}
}
