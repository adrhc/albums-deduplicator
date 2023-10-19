package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndex;

import java.io.IOException;

@ShellComponent("Files index management.")
@RequiredArgsConstructor
@Slf4j
public class IndexCommands {
	private final AppPaths appPaths;

	@ShellMethod(value = "Create the index at the provided path (remove it first, if existing).",
			key = {"create", "index"})
	public void create() throws IOException {
		filesIndex().createOrReplaceIndex();
		log.debug("\n{} index created!", appPaths.getIndexPath());
	}

	@ShellMethod(value = "Create the index at the provided path (remove it first, if existing).",
			key = {"find-dups", "find-duplicates"})
	public void findDuplicates() throws IOException {
		log.debug("\n{}", filesIndex().findDuplicates());
	}

	@Lookup
	protected FilesIndex filesIndex() {
		return null;
	}
}
