package ro.go.adrhc.deduplicator.datasource.index;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.util.Optional;

import static ro.go.adrhc.persistence.lucene.write.FieldFactory.storedAndAnalyzed;
import static ro.go.adrhc.persistence.lucene.write.FieldFactory.storedButNotAnalyzed;

@RequiredArgsConstructor
@Slf4j
public class FileMetadataToDocumentConverter {
	private final LuceneTokenizer luceneTokenizer;

	@NonNull
	public Optional<Document> convert(@NonNull FileMetadata metadata) {
		if (!metadata.hasPath()) {
			log.error("\nCan't index an empty path!\nskipping: {}", metadata);
			return Optional.empty();
		}

		Document doc = new Document();

		doc.add(storedButNotAnalyzed(IndexFieldType.filePath, metadata.getPath()));
		doc.add(storedAndAnalyzed(IndexFieldType.fileNameNoExt, metadata.getPath()));
		doc.add(storedButNotAnalyzed(IndexFieldType.size, metadata.getPath()));
		doc.add(storedButNotAnalyzed(IndexFieldType.lastModified, metadata.getLastModified()));

		return Optional.of(doc);
	}
}
