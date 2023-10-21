package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexCreateService;
import ro.go.adrhc.deduplicator.datasource.index.services.update.FilesIndexFullUpdateService;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Files index management.")
@RequiredArgsConstructor
@Slf4j
public class IndexCommands {
	private final AppPaths appPaths;

	@ShellMethod(value = "Create the index at the provided path (remove it first, if existing).",
			key = {"create", "index"})
	public void create() throws IOException {
		filesIndex().createOrReplace();
		log.debug("\n{} index created!", appPaths.getIndexPath());
	}

	@ShellMethod(value = "Update the index at the provided path.", key = {"update", "reindex"})
	public void update() {
		try {
			fullFilesIndexUpdateService().update();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.debug("\n{} index update failed!", appPaths.getIndexPath());
		}
	}

	@Lookup
	protected FilesIndexCreateService<Path, FileMetadata> filesIndex() {
		return null;
	}

	@Lookup
	protected FilesIndexFullUpdateService<Path, FileMetadata> fullFilesIndexUpdateService() {
		return null;
	}
}
