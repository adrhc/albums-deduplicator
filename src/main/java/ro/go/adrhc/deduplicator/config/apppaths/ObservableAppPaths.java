package ro.go.adrhc.deduplicator.config.apppaths;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ObservableAppPaths {
	@Getter
	private final AppPaths appPaths;
	private final List<IndexPathObserver> pathsObservers;

	public void update(Path indexPath, Path filesPath, Path duplicatesPath) {
		Path oldIndexPath = appPaths.getIndexPath();
		appPaths.update(indexPath, filesPath, duplicatesPath);
		if (!Objects.equals(appPaths.getIndexPath(), oldIndexPath)) {
			notifyObservers();
		}
	}

	protected void notifyObservers() {
		pathsObservers.forEach(IndexPathObserver::indexPathChanged);
	}
}
