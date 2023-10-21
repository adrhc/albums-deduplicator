package ro.go.adrhc.deduplicator.datasource.index.services.update;

import java.util.Collection;

/**
 * @param notIndexedIds      contains not indexed data identifiers
 * @param obsoleteIndexedIds contains indexed ids for which the indexed data no longer exist
 */
public record IndexChanges(Collection<String> notIndexedIds, Collection<String> obsoleteIndexedIds) {
	public boolean hasChanges() {
		return !notIndexedIds.isEmpty() || !obsoleteIndexedIds.isEmpty();
	}

	public int notIndexedSize() {
		return notIndexedIds.size();
	}

	public int indexIdsMissingDataSize() {
		return obsoleteIndexedIds.size();
	}
}
