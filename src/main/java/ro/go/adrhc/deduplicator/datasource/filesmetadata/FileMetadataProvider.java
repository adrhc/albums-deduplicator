package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@Component
public class FileMetadataProvider {
	public Collection<FileMetadata> loadAllMetadata() {
		return List.of();
	}

	public Collection<FileMetadata> loadMetadata(List<Path> paths) {
		return List.of();
	}
}
