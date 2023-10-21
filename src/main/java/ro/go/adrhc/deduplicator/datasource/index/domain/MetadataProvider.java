package ro.go.adrhc.deduplicator.datasource.index.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface MetadataProvider<MID, M> {
	List<MID> loadAllIds() throws IOException;

	List<M> loadAll() throws IOException;

	List<M> loadByIds(Collection<MID> paths);
}
