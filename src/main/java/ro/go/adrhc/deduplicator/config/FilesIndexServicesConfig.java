package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexServicesFactories;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.fsindex.IndexCreateService;
import ro.go.adrhc.persistence.lucene.index.update.IndexFullUpdateService;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexServicesConfig {
	private final AppPaths appPaths;
	private final FilesIndexServicesFactories filesIndexServicesFactories;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexCreateService filesIndex() {
		return filesIndexServicesFactories.createFilesIndexCreateService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return filesIndexServicesFactories.createFilesIndexDedupService(appPaths.getIndexPath(), appPaths.getFilesPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexFullUpdateService indexFullUpdateService() {
		return filesIndexServicesFactories.createFilesIndexFullUpdateService(appPaths.getIndexPath());
	}
}
