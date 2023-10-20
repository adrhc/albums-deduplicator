package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.persistence.lucene.FSTypedIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.FSTypedIndex.createFSIndex;
import static ro.go.adrhc.util.fn.SneakyFunctionUtils.toSneakyFunction;

@Component
@RequiredArgsConstructor
public class FilesIndexFactory {
	private final FilesIndexProperties indexProperties;
	private final LuceneTokenizer luceneTokenizer;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataToDocumentConverter toDocumentConverter;
	private final FileMetadataProvider fileMetadataProvider;

	public FilesIndex<Path, FileMetadata> create(Path indexPath) {
		return new FilesIndex<>(IndexFieldType.filePath.name(),
				toFileMetadataConverter, fileMetadataProvider, Path::of,
				createDocumentIndexReaderTemplate(indexPath),
				createFSTypedIndex(indexPath));
	}

	private FSTypedIndex<FileMetadata> createFSTypedIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(),
				toSneakyFunction(toDocumentConverter::convert), indexPath);
	}

	public DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}
}
