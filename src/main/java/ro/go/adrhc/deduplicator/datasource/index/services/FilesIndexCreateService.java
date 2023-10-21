package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.MetadataProvider;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class FilesIndexCreateService<MID, M> {
	private final MetadataProvider<MID, M> metadataProvider;
	private final FSTypedIndex<M> luceneIndex;

	public void createOrReplace() throws IOException {
		luceneIndex.createOrReplace(metadataProvider.loadAll());
	}
}
