package ro.go.adrhc.deduplicator.datasource.index.changes;

import java.util.Collection;

public interface ActualData<T> {
	boolean remove(String documentId);

	boolean isEmpty();

	int size();

	Collection<T> toCollection();
}
