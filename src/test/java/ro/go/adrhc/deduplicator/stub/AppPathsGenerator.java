package ro.go.adrhc.deduplicator.stub;

import lombok.experimental.UtilityClass;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableAppPaths;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;

@UtilityClass
public class AppPathsGenerator {
    public static void populateTestPaths(Path rootPath,
            ObservableAppPaths observableAppPaths) throws IOException {
        observableAppPaths.update(createDirectories(rootPath.resolve("Index")),
                createDirectories(rootPath.resolve("Files")), null);
    }
}
