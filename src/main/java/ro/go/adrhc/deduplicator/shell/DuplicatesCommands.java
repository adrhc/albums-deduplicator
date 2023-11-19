package ro.go.adrhc.deduplicator.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableAppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.deduplicator.services.FilesDedupService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Duplicates management.")
@Slf4j
public class DuplicatesCommands extends AbstractCommand {
	public DuplicatesCommands(ApplicationContext ac, ObservableAppPaths appPaths,
			IndexDataSource<Path, FileMetadata> indexDataSource) {
		super(ac, appPaths, indexDataSource);
	}

	@ShellMethod(value = "Find duplicates.", key = {"find-dups"})
	public void findDuplicates() throws IOException {
		log.debug("\n{}", filesDedupService().findDups());
	}

	@ShellMethod(value = "Remove the duplicates, update the index and show duplicates.", key = {"remove-dups"})
	public void removeDuplicates() throws IOException {
		if (filesDedupService().removeDups()) {
			fileMetadataRepository().restore(indexDataSource);
		}
		findDuplicates();
	}

	@Lookup
	protected FilesDedupService filesDedupService() {
		return null;
	}
}
