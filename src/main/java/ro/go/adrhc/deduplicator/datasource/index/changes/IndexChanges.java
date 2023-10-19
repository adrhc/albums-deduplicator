package ro.go.adrhc.deduplicator.datasource.index.changes;

import java.util.Collection;
import java.util.List;

/**
 * @param notIndexedActualData      exist on FS but not in the index
 * @param indexIdsMissingActualData exist in the index but not on FS
 */
public record IndexChanges<T>(ActualData<T> notIndexedActualData, List<String> indexIdsMissingActualData) {
	public boolean hasChanges() {
		return !notIndexedActualData.isEmpty() || !indexIdsMissingActualData.isEmpty();
	}

	public Collection<T> notIndexedActualDataCollection() {
		return notIndexedActualData.toCollection();
	}

	public int notIndexedActualDataSize() {
		return notIndexedActualData.size();
	}

	public int indexIdsMissingActualDataSize() {
		return indexIdsMissingActualData.size();
	}
}
