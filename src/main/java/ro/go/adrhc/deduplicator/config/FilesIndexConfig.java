package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexCreateService;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexFactories;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.deduplicator.datasource.index.services.update.FilesIndexFullUpdateService;

import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexConfig {
	private final AppPaths appPaths;
	private final FilesIndexFactories filesIndexFactories;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexCreateService<Path, FileMetadata> filesIndex() {
		return filesIndexFactories.createFilesIndexCreateService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return filesIndexFactories.createFilesIndexDedupService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexFullUpdateService<Path, FileMetadata> fullFilesIndexUpdateService() {
		return filesIndexFactories.createFilesIndexFullUpdateService(appPaths.getIndexPath());
	}
}
