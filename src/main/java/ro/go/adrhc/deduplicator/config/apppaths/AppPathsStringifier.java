package ro.go.adrhc.deduplicator.config.apppaths;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.text.StringUtils;

import java.nio.file.Path;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AppPathsStringifier {
    private final FileSystemUtils fsUtils;

    public String toString(ObservableAppPaths observableAppPaths) {
        return toString(observableAppPaths.getAppPaths());
    }

    public String toString(AppPaths appPaths) {
        return StringUtils.concat(toContextPathInfos(appPaths));
    }

    private Stream<AppPathDetails> toContextPathInfos(AppPaths appPaths) {
        return Stream.of(
                new AppPathDetails(appPaths.getIndexPathParent(), "index parent-path"),
                new AppPathDetails(appPaths.getIndexPath(), "index path"),
                new AppPathDetails(appPaths.getFilesPath(), "files path"),
                new AppPathDetails(appPaths.getDuplicatesPath(), "duplicates path"));
    }

    @RequiredArgsConstructor
    private class AppPathDetails {
        private final Path path;
        private final String description;

        @Override
        public String toString() {
            if (path == null) {
                return "%s is not set because is not provided!".formatted(description);
            } else {
                String pathAndDescription = "%s (%s) exists: ".formatted(path, description);
                return "%-74s%b".formatted(pathAndDescription, AppPathsStringifier.this.fsUtils.exists(path));
            }
        }
    }
}
