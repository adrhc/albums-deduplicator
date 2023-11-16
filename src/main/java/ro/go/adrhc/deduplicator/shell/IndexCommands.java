package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
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

	@Lookup
	protected IndexRepository<Path, FileMetadata> fileMetadataRepository() {
		return null;
	}
}
