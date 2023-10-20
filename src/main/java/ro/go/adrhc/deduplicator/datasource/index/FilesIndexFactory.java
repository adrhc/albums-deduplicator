package ro.go.adrhc.deduplicator.datasource.index;

import com.rainerhahnekamp.sneakythrow.functional.SneakyFunction;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.lib.LuceneFactories;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static ro.go.adrhc.deduplicator.lib.LuceneFactories.standardTokenizer;
import static ro.go.adrhc.persistence.lucene.FSLuceneIndex.createFSIndex;

@RequiredArgsConstructor
public class FilesIndexFactory {
	private final FilesIndexProperties indexProperties;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataProvider fileMetadataProvider;

	public FilesIndex<Path, FileMetadata> create(Path indexPath) {
		return new FilesIndex<>(IndexFieldType.filePath.name(),
				toFileMetadataConverter, fileMetadataProvider, Path::of,
				LuceneFactories.create(indexProperties, indexPath),
				createFSLuceneIndex(indexPath));
	}

	private FSLuceneIndex<FileMetadata> createFSLuceneIndex(Path indexPath) {
		LuceneTokenizer luceneTokenizer = standardTokenizer(indexProperties);
		return createFSIndex(IndexFieldType.filePath,
				luceneTokenizer, toDocumentConverter(luceneTokenizer), indexPath);
	}

	private SneakyFunction<FileMetadata, Optional<Document>, IOException>
	toDocumentConverter(LuceneTokenizer luceneTokenizer) {
		FileMetadataToDocumentConverter toDocumentConverter = new FileMetadataToDocumentConverter(luceneTokenizer);
		return am -> Optional.of(toDocumentConverter.convert(am));
	}
}
