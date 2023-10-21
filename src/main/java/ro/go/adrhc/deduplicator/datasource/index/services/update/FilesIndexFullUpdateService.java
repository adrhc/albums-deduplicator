package ro.go.adrhc.deduplicator.datasource.index.services.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;
import ro.go.adrhc.persistence.lucene.domain.MetadataProvider;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class FilesIndexFullUpdateService<MID, M> {
	private final String idField;
	private final MetadataProvider<MID, M> metadataProvider;
	private final Function<String, MID> metadataIdParser;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final FSTypedIndex<M> luceneIndex;

	public void update() throws IOException {
		IndexChanges<MID> changes = getIndexChanges();
		if (changes.hasChanges()) {
			applyIndexChanges(changes);
		} else {
			log.debug("\nNo changes detected!");
		}
	}

	private IndexChanges<MID> getIndexChanges() throws IOException {
		return indexReaderTemplate.transformFieldValues(idField, this::toIndexChanges);
	}

	private IndexChanges<MID> toIndexChanges(Stream<String> fieldStream) throws IOException {
		List<MID> mids = new ArrayList<>(metadataProvider.loadAllIds());
		List<String> docsToRemove = fieldStream
				.filter(id -> !mids.remove(metadataIdParser.apply(id))).toList();
		return new IndexChanges<>(mids, docsToRemove);
	}

	private void applyIndexChanges(IndexChanges<MID> changes) throws IOException {
		log.debug("\nremoving {} missing data from the index", changes.indexIdsMissingDataSize());
		luceneIndex.removeByIds(changes.indexIdsMissingData());
		log.debug("\nextracting {} metadata to index", changes.notIndexedSize());
		Collection<M> metadata = metadataProvider.loadByIds(changes.notIndexed());
		log.debug("\nadding {} metadata records to the index", metadata.size());
		luceneIndex.addItems(metadata);
		log.debug("\n{} index updated!", luceneIndex.getIndexPath());
	}
}
