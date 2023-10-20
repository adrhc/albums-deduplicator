package ro.go.adrhc.deduplicator.datasource.index.changes;

import com.rainerhahnekamp.sneakythrow.functional.SneakySupplier;
import lombok.RequiredArgsConstructor;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class IndexChangesProvider<T> {
	private final String idField;
	private final SneakySupplier<ActualData<T>, IOException> actualDataSupplier;
	private final DocumentIndexReaderTemplate indexReaderTemplate;

	public IndexChanges<T> getChanges() throws IOException {
		return indexReaderTemplate.transformFieldStream(idField, this::transformFieldStream);
	}

	private IndexChanges<T> transformFieldStream(Stream<String> fieldStream) throws IOException {
		ActualData<T> actualData = actualDataSupplier.get();
		List<String> docsToRemove = fieldStream.filter(id -> !actualData.remove(id)).toList();
		return new IndexChanges<>(actualData, docsToRemove);
	}
}
