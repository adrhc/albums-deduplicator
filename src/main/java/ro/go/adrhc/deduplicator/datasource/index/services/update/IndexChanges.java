package ro.go.adrhc.deduplicator.datasource.index.services.update;

import java.util.Collection;

/**
 * @param notIndexed          contains not indexed data
 * @param indexIdsMissingData contains indexed ids for which the indexed data no longer exist
 */
public record IndexChanges<T>(Collection<T> notIndexed, Collection<String> indexIdsMissingData) {
	public boolean hasChanges() {
		return !notIndexed.isEmpty() || !indexIdsMissingData.isEmpty();
	}

	public Collection<T> notIndexedActualDataCollection() {
		return notIndexed;
	}

	public int notIndexedActualDataSize() {
		return notIndexed.size();
	}

	public int indexIdsMissingActualDataSize() {
		return indexIdsMissingData.size();
	}
}
