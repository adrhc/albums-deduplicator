package ro.go.adrhc.deduplicator.datasource.index.domain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;

import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldFactory.storedAndAnalyzed;
import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldFactory.storedButNotAnalyzed;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataToDocumentConverter {
	@NonNull
	public Document convert(@NonNull FileMetadata metadata) {
		Document doc = new Document();

		doc.add(storedButNotAnalyzed(IndexFieldType.filePath, metadata.getPath()));
		doc.add(storedButNotAnalyzed(IndexFieldType.size, metadata.getSize()));
		doc.add(storedButNotAnalyzed(IndexFieldType.lastModified, metadata.getLastModified()));
		doc.add(storedButNotAnalyzed(IndexFieldType.fileHash, metadata.getFileHash()));

		filenameNoExt(metadata.getPath()).ifPresentOrElse(
				filenameNoExt -> doc.add(storedAndAnalyzed(IndexFieldType.fileNameNoExt, filenameNoExt)),
				() -> log.warn("\n{} has a weird name!", metadata.getPath()));

		return doc;
	}
}
