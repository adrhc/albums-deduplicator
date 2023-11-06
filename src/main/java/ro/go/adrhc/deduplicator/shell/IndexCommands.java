package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexCreateService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.DocumentsIndexRestoreService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.io.IOException;

@ShellComponent("Files index management.")
@RequiredArgsConstructor
@Slf4j
public class IndexCommands {
	private final FileMetadataProvider fileMetadataProvider;
	private final IndexDataSource<String, Document> indexDataSource;
	private final AppPaths appPaths;

	@ShellMethod(value = "Create the index at the provided path (remove it first, if existing).",
			key = {"create", "index"})
	public void create() throws IOException {
		typedIndexCreateService().createOrReplace(fileMetadataProvider.loadAll());
		log.debug("\n{} index created!", appPaths.getIndexPath());
	}

	@ShellMethod(value = "Update the index at the provided path.", key = {"update", "reindex"})
	public void update() {
		try {
			documentsIndexRestoreService().restore(indexDataSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.debug("\n{} index update failed!", appPaths.getIndexPath());
		}
	}

	@Lookup
	protected TypedIndexCreateService<FileMetadata> typedIndexCreateService() {
		return null;
	}

	@Lookup
	protected DocumentsIndexRestoreService<String, FileMetadata> documentsIndexRestoreService() {
		return null;
	}
}
