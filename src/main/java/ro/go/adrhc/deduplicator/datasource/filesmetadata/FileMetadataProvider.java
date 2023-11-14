package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static ro.go.adrhc.util.ConcurrencyUtils.safelyGetAll;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataProvider implements IndexDataSource<Path, FileMetadata> {
	private final FileMetadataFactory metadataFactory;
	private final ExecutorService metadataExecutorService;
	private final SimpleDirectory filesDirectory;

	@Override
	public Stream<FileMetadata> loadAll() throws IOException {
		return loadByIds(loadAllIds());
	}

	@Override
	public Stream<Path> loadAllIds() throws IOException {
		return filesDirectory.getAllPaths().stream();
	}

	@Override
	public Stream<FileMetadata> loadByIds(Stream<Path> paths) {
		// load the file paths and start metadata loading (using CompletableFuture)
		Stream<CompletableFuture<Optional<FileMetadata>>> futures = paths
				.flatMap(this::getContainedPaths)
				.map(this::asyncMetadataSupplier);
		// wait then get
		return safelyGetAll(futures).flatMap(Optional::stream);
	}

	private CompletableFuture<Optional<FileMetadata>> asyncMetadataSupplier(Path path) {
		return CompletableFuture.supplyAsync(() -> metadataFactory.create(path), metadataExecutorService);
	}

	private Stream<Path> getContainedPaths(Path path) {
		if (Files.isDirectory(path)) {
			try {
				return filesDirectory.getPaths(path).stream();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				return Stream.of();
			}
		} else {
			return Stream.of(path);
		}
	}
}
