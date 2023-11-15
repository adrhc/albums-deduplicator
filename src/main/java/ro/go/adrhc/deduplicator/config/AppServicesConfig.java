package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.deduplicator.services.FilesDedupService;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class AppServicesConfig {
	private final SimpleDirectory duplicatesDirectory;
	private final AppPaths appPaths;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesDedupService filesDedupService(
			IndexRepository<Path, FileMetadata> fileMetadataRepository) {
		return new FilesDedupService(duplicatesDirectory,
				fileMetadataRepository, appPaths.getFilesPath());
	}
}
