package ro.go.adrhc.deduplicator.datasource.index.services.update;

import java.util.Collection;

/**
 * @param notIndexedActualData      exist on FS but not in the index
 * @param indexIdsMissingActualData exist in the index but not on FS
 */
public record IndexChanges<T>(Collection<T> notIndexedActualData, Collection<String> indexIdsMissingActualData) {
	public boolean hasChanges() {
		return !notIndexedActualData.isEmpty() || !indexIdsMissingActualData.isEmpty();
	}

	public Collection<T> notIndexedActualDataCollection() {
		return notIndexedActualData;
	}

	public int notIndexedActualDataSize() {
		return notIndexedActualData.size();
	}

	public int indexIdsMissingActualDataSize() {
		return indexIdsMissingActualData.size();
	}
}
