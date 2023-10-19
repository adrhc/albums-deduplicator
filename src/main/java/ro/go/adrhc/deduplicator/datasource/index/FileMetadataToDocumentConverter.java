package ro.go.adrhc.deduplicator.datasource.index;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static ro.go.adrhc.persistence.lucene.write.FieldFactory.storedAndAnalyzed;
import static ro.go.adrhc.persistence.lucene.write.FieldFactory.storedButNotAnalyzed;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;

@RequiredArgsConstructor
@Slf4j
public class FileMetadataToDocumentConverter {
	private final LuceneTokenizer luceneTokenizer;

	@NonNull
	public Optional<Document> convert(@NonNull FileMetadata metadata) {
		Document doc = new Document();

		doc.add(storedButNotAnalyzed(IndexFieldType.filePath, metadata.getPath()));
		doc.add(storedButNotAnalyzed(IndexFieldType.size, metadata.getSize()));
		doc.add(storedButNotAnalyzed(IndexFieldType.lastModified, metadata.getLastModified()));
		doc.add(storedButNotAnalyzed(IndexFieldType.fileHash, metadata.getFileHash()));

		Optional<Field> filenameNoExt = filenameNoExt(metadata.getPath()).flatMap(this::toField);
		if (filenameNoExt.isPresent()) {
			doc.add(filenameNoExt.get());
			return Optional.of(doc);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * this part has to be synchronized with SearchedFileToQueryConverter (tokenizedTitle part)
	 */
	private Optional<Field> toField(String filenameNoExt) {
		Set<String> tokens;
		try {
			tokens = luceneTokenizer.tokenize(filenameNoExt);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
		if (tokens.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(storedAndAnalyzed(IndexFieldType.fileNameNoExt, tokens));
		}
	}
}
