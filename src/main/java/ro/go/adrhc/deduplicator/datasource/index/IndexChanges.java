package ro.go.adrhc.deduplicator.datasource.index;

import java.nio.file.Path;
import java.util.List;

/**
 * @param fsPresentNotIndexedPaths exist on FS but not in the index
 * @param fsMissingIndexedPaths    exist in the index but not on FS
 */
public record IndexChanges(List<Path> fsPresentNotIndexedPaths, List<String> fsMissingIndexedPaths) {
	public static IndexChanges empty() {
		return new IndexChanges(List.of(), List.of());
	}

	public boolean hasChanges() {
		return !fsPresentNotIndexedPaths.isEmpty() || !fsMissingIndexedPaths.isEmpty();
	}
}
