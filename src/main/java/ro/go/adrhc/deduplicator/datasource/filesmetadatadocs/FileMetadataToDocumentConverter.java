package ro.go.adrhc.deduplicator.datasource.filesmetadatadocs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldFactory.storedAndAnalyzed;
import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldFactory.storedButNotAnalyzed;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileMetadataToDocumentConverter {
	private final LuceneTokenizer luceneTokenizer;

	@NonNull
	public Document convert(@NonNull FileMetadata metadata) {
		Document doc = new Document();

		doc.add(storedButNotAnalyzed(IndexFieldType.filePath, metadata.getPath()));
		doc.add(storedButNotAnalyzed(IndexFieldType.size, metadata.getSize()));
		doc.add(storedButNotAnalyzed(IndexFieldType.lastModified, metadata.getLastModified()));
		doc.add(storedButNotAnalyzed(IndexFieldType.fileHash, metadata.getFileHash()));

		filenameNoExtTokens(metadata).ifPresentOrElse(
				tokens -> doc.add(storedAndAnalyzed(IndexFieldType.fileNameNoExt, tokens)),
				() -> log.warn("\n{} has a weird name!", metadata.getPath()));

		return doc;
	}

	private Optional<Set<String>> filenameNoExtTokens(FileMetadata metadata) {
		return filenameNoExt(metadata.getPath()).flatMap(this::toTokens);
	}

	private Optional<Set<String>> toTokens(String filenameNoExt) {
		try {
			return Optional.of(luceneTokenizer.tokenize(filenameNoExt));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
	}
}
