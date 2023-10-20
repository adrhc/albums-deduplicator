package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndex;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndexFactory;

import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexConfig {
	private final AppPaths appPaths;
	private final FilesIndexFactory filesIndexFactory;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndex<Path, FileMetadata> filesIndex() {
		return filesIndexFactory.create(appPaths.getIndexPath());
	}
}
