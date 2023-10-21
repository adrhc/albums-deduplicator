package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.persistence.lucene.core.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static ro.go.adrhc.util.fn.SneakyBiFunctionUtils.curry;

@RequiredArgsConstructor
public class FilesIndexReaderTemplate {
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final DocumentIndexReaderTemplate indexReaderTemplate;

	public <R> R transformFileMetadata(Function<Stream<FileMetadata>, R> transformer) throws IOException {
		return indexReaderTemplate.transformDocuments(curry(this::doTransformFileMetadata, transformer));
	}

	private <R> R doTransformFileMetadata(
			Function<Stream<FileMetadata>, R> transformer, Stream<Document> documents) {
		return transformer.apply(documents.map(toFileMetadataConverter::convert));
	}
}
