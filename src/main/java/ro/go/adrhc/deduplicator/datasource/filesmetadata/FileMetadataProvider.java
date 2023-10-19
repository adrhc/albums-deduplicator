package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static ro.go.adrhc.util.ConcurrencyUtils.safelyGetAll;

@RequiredArgsConstructor
public class FileMetadataProvider {
	private final ExecutorService metadataExecutorService;
	private final SimpleDirectory filesDirectory;
	private final FileMetadataFactory metadataFactory;

	public List<FileMetadata> loadAllMetadata() {
		return loadMetadata(filesDirectory.getAllPaths());
	}

	public List<FileMetadata> loadMetadata(Collection<Path> paths) {
		// load the file paths and start metadata loading (using CompletableFuture)
		Stream<CompletableFuture<Optional<FileMetadata>>> futures = paths.stream()
				.flatMap(path -> Files.isDirectory(path) ?
						filesDirectory.getPaths(path).stream() : Stream.of(path))
				.map(this::asyncAudioMetadataSupplierOf);
		// wait then get
		return safelyGetAll(futures).flatMap(Optional::stream).toList();
	}

	private CompletableFuture<Optional<FileMetadata>> asyncAudioMetadataSupplierOf(Path path) {
		return CompletableFuture.supplyAsync(() -> metadataFactory.create(path), metadataExecutorService);
	}
}
