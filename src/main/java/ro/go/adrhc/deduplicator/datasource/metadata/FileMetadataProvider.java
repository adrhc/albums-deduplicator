package ro.go.adrhc.deduplicator.datasource.metadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.concurrency.CompletableFuturesToOutcomeStreamConverter;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataProvider implements IndexDataSource<Path, FileMetadata> {
	private final ExecutorService metadataExecutorService;
	private final CompletableFuturesToOutcomeStreamConverter futuresToStreamConverter;
	private final FileMetadataLoader metadataFactory;
	private final SimpleDirectory filesDirectory;

	@Override
	public Stream<Path> loadAllIds() throws IOException {
		return filesDirectory.getAllPaths().stream();
	}

	@Override
	public Stream<FileMetadata> loadByIds(Stream<Path> ids) {
		return ids.flatMap(this::safelyLoadByPath);
	}

	@Override
	public Stream<FileMetadata> loadAll() throws IOException {
		return loadByStartPath(filesDirectory.getRoot());
	}

	protected Stream<FileMetadata> safelyLoadByPath(Path startPath) {
		try {
			return loadByStartPath(startPath);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Stream.empty();
		}
	}

	protected Stream<FileMetadata> loadByStartPath(Path startPath) throws IOException {
		return toFileMetadataStream(filesDirectory.getPaths(startPath));
	}

	protected Stream<FileMetadata> toFileMetadataStream(Collection<Path> filePaths) {
		return futuresToStreamConverter
				.toStream(filePaths.stream().map(this::loadMetadata))
				.flatMap(Optional::stream);
	}

	protected CompletableFuture<Optional<FileMetadata>> loadMetadata(Path filePath) {
		return CompletableFuture.supplyAsync(() -> doLoadMetadata(filePath), metadataExecutorService);
	}

	private Optional<FileMetadata> doLoadMetadata(Path path) {
		try {
			return metadataFactory.load(path);
		} finally {
			log.debug("\nloaded metadata for: {}", path);
		}
	}
}
