package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.deduplicator.datasource.index.services.update.FilesIndexFullUpdateService;

import java.io.IOException;

@ShellComponent("Duplicates management.")
@RequiredArgsConstructor
@Slf4j
public class DuplicatesCommands {
	@ShellMethod(value = "Find duplicates.", key = {"find-dups"})
	public void findDuplicates() throws IOException {
		log.debug("\n{}", filesIndexDuplicatesMngmtService().find());
	}

	@ShellMethod(value = "Remove the duplicates, update the index and show duplicates.", key = {"remove-dups"})
	public void removeDuplicates() throws IOException {
		if (filesIndexDuplicatesMngmtService().removeDups()) {
			fullFilesIndexUpdateService().update();
		}
		findDuplicates();
	}

	@Lookup
	protected FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return null;
	}

	@Lookup
	protected FilesIndexFullUpdateService fullFilesIndexUpdateService() {
		return null;
	}
}
