package ro.go.adrhc.deduplicator.datasource.index.domain;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.persistence.lucene.domain.DocumentsProvider;
import ro.go.adrhc.util.conversion.ConversionUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileMetadataDocumentsProvider implements DocumentsProvider {
	private final FileMetadataProvider metadataProvider;
	private final FileMetadataToDocumentConverter toDocumentConverter;

	@Override
	public List<Document> loadAll() throws IOException {
		Collection<FileMetadata> metadata = metadataProvider.loadAll();
		return ConversionUtils.convertAll(toDocumentConverter::convert, metadata);
	}

	@Override
	public List<String> loadAllIds() throws IOException {
		Collection<Path> paths = metadataProvider.loadAllIds();
		return ConversionUtils.convertAll(Path::toString, paths);
	}

	@Override
	public List<Document> loadByIds(Collection<String> collection) {
		Collection<Path> paths = ConversionUtils.convertAll(Path::of, collection);
		List<FileMetadata> metadata = metadataProvider.loadByIds(paths);
		return ConversionUtils.convertAll(toDocumentConverter::convert, metadata);
	}
}
