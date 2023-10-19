package ro.go.adrhc.deduplicator.stub;

import lombok.experimental.UtilityClass;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;

@UtilityClass
public class AppPathsGenerator {
	public static void populateTestPaths(Path rootPath, AppPaths appPaths) throws IOException {
		appPaths.setIndexPath(createDirectories(rootPath.resolve("Index")));
		appPaths.setFilesPath(createDirectories(rootPath.resolve("Files")));
	}
}
