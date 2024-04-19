package ro.go.adrhc.deduplicator.datasource.metadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.io.FilesMetadataLoader;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataProvider implements IndexDataSource<Path, FileMetadata> {
    private final SimpleDirectory filesDirectory;
    private final FilesMetadataLoader<Optional<FileMetadata>> filesMetadataLoader;

    @Override
    public Stream<Path> loadAllIds() throws IOException {
        return filesDirectory.getAllPaths().stream();
    }

    @Override
    public Stream<FileMetadata> loadByIds(Stream<Path> paths) {
        return filesMetadataLoader.loadByPaths(paths).flatMap(Optional::stream);
    }

    @Override
    public Stream<FileMetadata> loadAll() {
        return filesMetadataLoader.loadAll().flatMap(Optional::stream);
    }
}
