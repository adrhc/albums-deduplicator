package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexFactories;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexCreateService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.DocumentsIndexRestoreService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexFactoriesConfig {
	private final AppPaths appPaths;
	private final FilesIndexFactories filesIndexFactories;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public TypedIndexCreateService<FileMetadata> typedIndexCreateService() {
		return filesIndexFactories.createCreateService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDedupService() {
		return filesIndexFactories.createDedupService(
				appPaths.getIndexPath(), appPaths.getFilesPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public DocumentsIndexRestoreService<String, FileMetadata> documentsIndexRestoreService() {
		return filesIndexFactories.createIndexRestoreService(appPaths.getIndexPath());
	}

	@Bean
	public IndexDataSource<String, Document> indexDataSource() {
		return filesIndexFactories.createIndexDataSource();
	}
}
