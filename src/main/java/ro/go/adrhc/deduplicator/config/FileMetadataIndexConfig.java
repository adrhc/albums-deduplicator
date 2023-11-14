package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.deduplicator.datasource.index.services.repository.ScopedTypedIndexFactoriesParams;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FileMetadataIndexConfig {
	private final ScopedTypedIndexFactoriesParams scopedParams;
	private final SimpleDirectory duplicatesDirectory;
	private final AppPaths appPaths;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexRepository<Path, FileMetadata> fileMetadataRepository() throws IOException {
		return scopedParams.getIndexRepository();
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndexDedupService filesIndexDedupService() throws IOException {
		return new FilesIndexDedupService(duplicatesDirectory, appPaths.getFilesPath(), fileMetadataRepository());
	}
}
