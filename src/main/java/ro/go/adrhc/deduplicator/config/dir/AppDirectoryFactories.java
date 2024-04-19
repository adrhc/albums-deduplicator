package ro.go.adrhc.deduplicator.config.dir;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.AppProperties;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.io.SimpleDirectory;

@Component
@RequiredArgsConstructor
public class AppDirectoryFactories {
    private final AppProperties appProperties;
    private final AppPaths appPaths;
    private final FileSystemUtils fsUtils;

    public SimpleDirectory duplicatesDirectory() {
        return SimpleDirectory.of(fsUtils, appPaths::getDuplicatesPath,
                appProperties.getSupportedExtensions()::supports);
    }

    public SimpleDirectory filesDirectory() {
        return SimpleDirectory.of(fsUtils, appPaths::getFilesPath,
                appProperties.getSupportedExtensions()::supports);
    }
}
