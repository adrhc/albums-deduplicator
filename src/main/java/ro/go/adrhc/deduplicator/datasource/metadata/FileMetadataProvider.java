package ro.go.adrhc.deduplicator.datasource.metadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.concurrency.AsyncStream;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataProvider implements IndexDataSource<Path, FileMetadata> {
	private final FileMetadataLoader metadataFactory;
	private final ExecutorService metadataExecutorService;
	private final SimpleDirectory filesDirectory;

	@Override
	public Stream<Path> loadAllIds() throws IOException {
		return filesDirectory.getAllPaths().stream();
	}

	@Override
	public Stream<FileMetadata> loadByIds(Stream<Path> ids) {
		return metadataAsyncStream()
				.toStream(mc -> ids.flatMap(p -> traverseAndAsyncConsume(mc, p)));
	}

	@Override
	public Stream<FileMetadata> loadAll() {
		return metadataAsyncStream()
				.toStream(mc -> traverseAndAsyncConsume(mc, filesDirectory.getRoot()));
	}

	private Stream<CompletableFuture<?>> traverseAndAsyncConsume(
			Consumer<FileMetadata> metadataConsumer, Path path) {
		if (Files.isDirectory(path)) {
			try {
				return filesDirectory.getPaths(path).stream()
						.flatMap(p -> traverseAndAsyncConsume(metadataConsumer, p));
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				return Stream.of();
			}
		} else {
			return Stream.of(asyncPathConsumer(metadataConsumer, path));
		}
	}

	private CompletableFuture<?> asyncPathConsumer(
			Consumer<FileMetadata> metadataConsumer, Path path) {
		return CompletableFuture.runAsync(() ->
				doWithPath(metadataConsumer, path), metadataExecutorService);
	}

	private void doWithPath(Consumer<FileMetadata> metadataConsumer, Path path) {
		log.info("\nloading metadata: {}", path.getFileName().toString());
		metadataFactory.load(path).ifPresent(metadataConsumer);
	}

	@Lookup
	protected AsyncStream<FileMetadata> metadataAsyncStream() {
		return null;
	}
}
