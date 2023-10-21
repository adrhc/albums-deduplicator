package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexServiceFactories;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.services.IndexCreateService;
import ro.go.adrhc.persistence.lucene.services.update.IndexFullUpdateService;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexConfig {
	private final AppPaths appPaths;
	private final FilesIndexServiceFactories filesIndexServiceFactories;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexCreateService filesIndex() {
		return filesIndexServiceFactories.createFilesIndexCreateService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return filesIndexServiceFactories.createFilesIndexDedupService(appPaths.getIndexPath(), appPaths.getFilesPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexFullUpdateService indexFullUpdateService() {
		return filesIndexServiceFactories.createFilesIndexFullUpdateService(appPaths.getIndexPath());
	}
}
