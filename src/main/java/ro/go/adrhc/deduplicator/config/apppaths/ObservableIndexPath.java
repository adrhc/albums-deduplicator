package ro.go.adrhc.deduplicator.config.apppaths;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ObservableIndexPath {
	@Getter
	private final AppPaths appPaths;
	private final List<IndexPathObserver> pathsObservers;

	public void update(Path indexPath, Path filesPath, Path duplicatesPath) {
		appPaths.update(indexPath, filesPath, duplicatesPath);
		notifyObservers();
	}

	protected void notifyObservers() {
		pathsObservers.forEach(IndexPathObserver::indexPathChanged);
	}
}
