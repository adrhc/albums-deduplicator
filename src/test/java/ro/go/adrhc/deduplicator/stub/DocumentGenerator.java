package ro.go.adrhc.deduplicator.stub;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.serde.FileMetadataToDocumentConverter;

import java.io.IOException;

@Component
public class DocumentGenerator {
	public Document create() throws IOException {
		return toDocumentConverter().convert(FileMetadataGenerator.create());
	}

	@Lookup
	protected FileMetadataToDocumentConverter toDocumentConverter() {
		return null;
	}
}
