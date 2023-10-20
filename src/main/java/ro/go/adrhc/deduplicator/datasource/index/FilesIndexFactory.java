package ro.go.adrhc.deduplicator.datasource.index;

import com.rainerhahnekamp.sneakythrow.functional.SneakyFunction;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.ActualData;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChanges;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChangesProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.lib.LuceneFactories;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
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

	public FilesIndex create(Path indexPath) {
		DocumentIndexReaderTemplate indexReaderTemplate = LuceneFactories.create(indexProperties, indexPath);
		return new FilesIndex(
				toFileMetadataConverter,
				fileMetadataProvider,
				indexReaderTemplate,
				createFSLuceneIndex(indexPath),
				fsIndexChangesProvider(indexReaderTemplate));
	}

	private SneakyFunction<ActualData<Path>, IndexChanges<Path>, IOException>
	fsIndexChangesProvider(DocumentIndexReaderTemplate indexReaderTemplate) {
		return (ActualData<Path> actualData) -> new IndexChangesProvider<Path>
				(IndexFieldType.filePath.name(), indexReaderTemplate).getChanges(actualData);
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
