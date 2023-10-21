package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndexDuplicatesMngmtService;
import ro.go.adrhc.deduplicator.datasource.index.FullFilesIndexUpdateService;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Duplicates management.")
@RequiredArgsConstructor
@Slf4j
public class DuplicatesCommands {
	private final AppPaths appPaths;

	@ShellMethod(value = "Find duplicates.", key = {"find-dups"})
	public void findDuplicates() throws IOException {
		log.debug("\n{}", filesIndexDuplicatesMngmtService().find());
	}

	@ShellMethod(value = "Remove the duplicates then update the index.", key = {"remove-dups"})
	public void removeDuplicates() throws IOException {
		if (filesIndexDuplicatesMngmtService().removeDups()) {
			fullFilesIndexUpdateService().update();
		}
	}

	@Lookup
	protected FilesIndexDuplicatesMngmtService filesIndexDuplicatesMngmtService() {
		return null;
	}

	@Lookup
	protected FullFilesIndexUpdateService<Path, FileMetadata> fullFilesIndexUpdateService() {
		return null;
	}
}
