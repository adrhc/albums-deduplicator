package ro.go.adrhc.deduplicator.datasource.index.changes;

import lombok.RequiredArgsConstructor;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static ro.go.adrhc.util.fn.SneakyBiFunctionUtils.curry;

@RequiredArgsConstructor
public class IndexChangesProvider<T> {
	private final String idField;
	private final DocumentIndexReaderTemplate indexReaderTemplate;

	public IndexChanges<T> getChanges(ActualData<T> actualData) throws IOException {
		return indexReaderTemplate.transformFieldStream(idField, curry(this::transformFieldStream, actualData));
	}

	private IndexChanges<T> transformFieldStream(ActualData<T> actualData, Stream<String> fieldStream) {
		List<String> docsToRemove = fieldStream.filter(id -> !actualData.remove(id)).toList();
		return new IndexChanges<>(actualData, docsToRemove);
	}
}
