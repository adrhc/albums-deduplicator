package ro.go.adrhc.deduplicator.stub;

import lombok.experimental.UtilityClass;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableIndexPath;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;

@UtilityClass
public class AppPathsGenerator {
	public static void populateTestPaths(Path rootPath,
			ObservableIndexPath observableIndexPath) throws IOException {
		observableIndexPath.update(createDirectories(rootPath.resolve("Index")),
				createDirectories(rootPath.resolve("Files")), null);
	}
}
