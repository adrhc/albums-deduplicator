package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProviderFactory;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndex;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndexFactory;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexConfig {
	private final AppPaths appPaths;
	private final AppProperties appProperties;
	private final FilesIndexProperties indexProperties;
	private final FileSystemUtils fsUtils;
	private final FileMetadataProviderFactory fileMetadataProviderFactory;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndex<Path, FileMetadata> filesIndex() {
		return filesIndexFactory().create(appPaths.getIndexPath());
	}

	@Bean
	public FilesIndexFactory filesIndexFactory() {
		return new FilesIndexFactory(indexProperties, toFileMetadataConverter,
				fileMetadataProviderFactory.create(filesDirectory()));
	}

	@Bean
	public SimpleDirectory filesDirectory() {
		return SimpleDirectory.of(fsUtils, appPaths::getFilesPath,
				appProperties.getSupportedExtensions()::supports);
	}
}
