package ro.go.adrhc.deduplicator.datasource.filesmetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
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
@Slf4j
public class FileMetadataProvider {
	private final ExecutorService metadataExecutorService;
	private final SimpleDirectory filesDirectory;
	private final FileMetadataFactory metadataFactory;

	public List<FileMetadata> loadAllMetadata() throws IOException {
		return loadMetadata(filesDirectory.getAllPaths());
	}

	public List<FileMetadata> loadMetadata(Collection<Path> paths) {
		// load the file paths and start metadata loading (using CompletableFuture)
		Stream<CompletableFuture<Optional<FileMetadata>>> futures = paths.stream()
				.flatMap(this::getContainedPaths)
				.map(this::asyncAudioMetadataSupplierOf);
		// wait then get
		return safelyGetAll(futures).flatMap(Optional::stream).toList();
	}

	private CompletableFuture<Optional<FileMetadata>> asyncAudioMetadataSupplierOf(Path path) {
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
