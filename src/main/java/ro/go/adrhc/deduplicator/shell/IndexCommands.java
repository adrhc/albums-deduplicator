package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Files index management.")
@RequiredArgsConstructor
@Slf4j
public class IndexCommands {
	protected final ApplicationContext ac;
	private final IndexDataSource<Path, FileMetadata> indexDataSource;
	private final AppPaths appPaths;

	@ShellMethod(key = {"create", "reset"},
			value = "Create the index at the provided path (remove it first, if exists).")
	public void reset() throws IOException {
		fileMetadataRepository().reset(indexDataSource.loadAll());
		log.debug("\n{} index created!", appPaths.getIndexPath());
	}

	@ShellMethod("Get the index size.")
	public void count() throws IOException {
		log.debug("\nindex size is {}", fileMetadataRepository().count());
	}

	@ShellMethod(value = "Update the index at the provided path.", key = {"update", "reindex"})
	public void update() {
		try {
			fileMetadataRepository().restore(indexDataSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.debug("\n{} index update failed!", appPaths.getIndexPath());
		}
	}

	/**
	 * solves 2nd call to count(), otherwise (from 2nd call on):
	 * No qualifying bean of type 'ro.go.adrhc.persistence.lucene.typedindex.IndexRepository
	 * <java.nio.file.Path, ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata>' available
	 */
	protected IndexRepository<Path, FileMetadata> fileMetadataRepository() {
		return ac.getBean(IndexRepository.class);
	}
}
