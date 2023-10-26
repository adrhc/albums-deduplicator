package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.services.FilesIndexFactories;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.persistence.lucene.fsindex.FSIndexCreateService;
import ro.go.adrhc.persistence.lucene.index.restore.DSIndexRestoreService;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexFactoriesConfig {
	private final AppPaths appPaths;
	private final FilesIndexFactories filesIndexFactories;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FSIndexCreateService filesIndex() {
		return filesIndexFactories.createCreateService(appPaths.getIndexPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return filesIndexFactories.createDedupService(
				appPaths.getIndexPath(), appPaths.getFilesPath());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public DSIndexRestoreService dsIndexRestoreService() {
		return filesIndexFactories.createIndexRestoreService(appPaths.getIndexPath());
	}
}
