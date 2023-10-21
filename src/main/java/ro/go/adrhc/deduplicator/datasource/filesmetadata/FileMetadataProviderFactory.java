package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.AppDirectoryFactories;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class FileMetadataProviderFactory {
	private final ExecutorService metadataExecutorService;
	private final AppDirectoryFactories appDirectoryFactories;
	private final FileMetadataFactory metadataFactory;

	public FileMetadataProvider create() {
		return new FileMetadataProvider(metadataFactory, metadataExecutorService,
				appDirectoryFactories.filesDirectory());
	}
}
