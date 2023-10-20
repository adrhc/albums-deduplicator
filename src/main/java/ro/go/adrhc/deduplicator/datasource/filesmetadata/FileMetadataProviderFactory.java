package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class FileMetadataProviderFactory {
	private final ExecutorService metadataExecutorService;
	private final FileMetadataFactory metadataFactory;

	public FileMetadataProvider create(SimpleDirectory filesDirectory) {
		return new FileMetadataProvider(metadataFactory, metadataExecutorService, filesDirectory);
	}
}
