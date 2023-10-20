package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.AppProperties;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class FileMetadataProviderFactory {
	private final AppPaths appPaths;
	private final AppProperties appProperties;
	private final FileSystemUtils fsUtils;
	private final ExecutorService metadataExecutorService;
	private final FileMetadataFactory metadataFactory;

	public FileMetadataProvider create() {
		return new FileMetadataProvider(metadataFactory, metadataExecutorService, filesDirectory());
	}

	private SimpleDirectory filesDirectory() {
		return SimpleDirectory.of(fsUtils, appPaths::getFilesPath,
				appProperties.getSupportedExtensions()::supports);
	}
}
