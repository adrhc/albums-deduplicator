package ro.go.adrhc.deduplicator.config.contextpaths;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.text.StringUtils;

import java.nio.file.Path;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ContextPathsStringifier {
	private final FileSystemUtils fsUtils;

	public String toString(ContextPaths contextPaths) {
		return StringUtils.concat(toContextPathInfos(contextPaths));
	}

	private Stream<ContextPathDetails> toContextPathInfos(ContextPaths contextPaths) {
		return Stream.of(
				new ContextPathDetails(contextPaths.getIndexPathParent(), "index parent-path"),
				new ContextPathDetails(contextPaths.getIndexPath(), "index path"),
				new ContextPathDetails(contextPaths.getFilesPath(), "files path"));
	}

	@RequiredArgsConstructor
	private class ContextPathDetails {
		private final Path path;
		private final String description;

		@Override
		public String toString() {
			if (path == null) {
				return "%s is not set because is not provided!".formatted(description);
			} else {
				String pathAndDescription = "%s (%s) exists: ".formatted(path, description);
				return "%-74s%b".formatted(pathAndDescription, ContextPathsStringifier.this.fsUtils.exists(path));
			}
		}
	}
}
