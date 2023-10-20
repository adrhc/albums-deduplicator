package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.MetadataProvider;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static ro.go.adrhc.util.fn.SneakyBiFunctionUtils.curry;

@RequiredArgsConstructor
@Slf4j
public class FullFilesIndexUpdateService<MID, M> {
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
		return indexReaderTemplate.transformFieldStream(idField,
				curry(this::transformFieldStream, new ArrayList<>(metadataProvider.loadAllIds())));
	}

	private IndexChanges<MID> transformFieldStream(List<MID> paths, Stream<String> fieldStream) {
		List<String> docsToRemove = fieldStream.filter(id -> !paths.remove(metadataIdParser.apply(id))).toList();
		return new IndexChanges<>(paths, docsToRemove);
	}

	private void applyIndexChanges(IndexChanges<MID> changes) throws IOException {
		log.debug("\nremoving {} missing songs from the index", changes.indexIdsMissingActualDataSize());
		luceneIndex.removeByIds(changes.indexIdsMissingActualData());
		log.debug("\nloading {} new songs metadata", changes.notIndexedActualDataSize());
		Collection<M> fileMetadata = metadataProvider
				.loadByIds(changes.notIndexedActualDataCollection());
		log.debug("\nadding {} metadata records to the index", fileMetadata.size());
		luceneIndex.addItems(fileMetadata);
		log.debug("\n{} index updated!", luceneIndex.getIndexPath());
	}
}
