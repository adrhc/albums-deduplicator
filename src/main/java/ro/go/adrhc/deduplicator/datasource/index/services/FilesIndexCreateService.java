package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.deduplicator.datasource.index.domain.MetadataProvider;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;

import java.io.IOException;

@RequiredArgsConstructor
public class FilesIndexCreateService<MID, M> {
	private final MetadataProvider<MID, M> metadataProvider;
	private final FSTypedIndex<M> fsTypedIndex;

	public void createOrReplace() throws IOException {
		fsTypedIndex.createOrReplace(metadataProvider.loadAll());
	}
}
