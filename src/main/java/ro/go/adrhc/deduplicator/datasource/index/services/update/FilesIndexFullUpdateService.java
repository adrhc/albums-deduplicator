package ro.go.adrhc.deduplicator.datasource.index.services.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.domain.DocumentsProvider;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class FilesIndexFullUpdateService {
	private final String idField;
	private final DocumentsProvider documentsProvider;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final FSLuceneIndex fsLuceneIndex;

	public void update() throws IOException {
		IndexChanges changes = getIndexChanges();
		if (changes.hasChanges()) {
			applyIndexChanges(changes);
		} else {
			log.debug("\nNo changes detected!");
		}
	}

	private IndexChanges getIndexChanges() throws IOException {
		return indexReaderTemplate.transformFieldValues(idField, this::toIndexChanges);
	}

	private IndexChanges toIndexChanges(Stream<String> indexedIds) throws IOException {
		List<String> ids = new ArrayList<>(documentsProvider.loadAllIds());
		List<String> docsToRemove = indexedIds.filter(id -> !ids.remove(id)).toList();
		return new IndexChanges(ids, docsToRemove);
	}

	private void applyIndexChanges(IndexChanges changes) throws IOException {
		log.debug("\nremoving {} missing data from the index", changes.indexIdsMissingDataSize());
		fsLuceneIndex.removeByIds(changes.obsoleteIndexedIds());
		log.debug("\nextracting {} metadata to index", changes.notIndexedSize());
		Collection<Document> documents = documentsProvider.loadByIds(changes.notIndexedIds());
		log.debug("\nadding {} metadata records to the index", documents.size());
		fsLuceneIndex.addDocuments(documents);
		log.debug("\n{} index updated!", fsLuceneIndex.getIndexPath());
	}
}
