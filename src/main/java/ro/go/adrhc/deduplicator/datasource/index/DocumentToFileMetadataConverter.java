package ro.go.adrhc.deduplicator.datasource.index;

import lombok.NonNull;
import org.apache.lucene.document.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;

import java.nio.file.Path;
import java.time.Instant;

@Component
public class DocumentToFileMetadataConverter implements Converter<Document, FileMetadata> {
	@NonNull
	public FileMetadata convert(@NonNull Document document) {
		return new FileMetadata(
				Path.of(document.get(IndexFieldType.filePath.name())),
				Instant.parse(document.get(IndexFieldType.lastModified.name())),
				Long.parseLong(document.get(IndexFieldType.size.name())),
				document.get(IndexFieldType.fileHash.name()));
	}
}
