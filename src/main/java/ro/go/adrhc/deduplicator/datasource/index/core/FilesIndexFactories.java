package ro.go.adrhc.deduplicator.datasource.index.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.LuceneFactories;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataToDocumentConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.persistence.lucene.fsindex.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;
import ro.go.adrhc.persistence.lucene.index.spi.DocumentsDatasource;
import ro.go.adrhc.persistence.lucene.typedindex.core.DefaultDocumentsDatasource;
import ro.go.adrhc.persistence.lucene.typedindex.spi.RawDataIdToStringConverter;
import ro.go.adrhc.persistence.lucene.typedindex.spi.RawDataToDocumentConverter;
import ro.go.adrhc.persistence.lucene.typedindex.spi.StringToRawDataIdConverter;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.fsindex.LuceneIndexFactories.createFSIndex;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	private final LuceneFactories luceneFactories;
	private final LuceneTokenizer luceneTokenizer;
	private final FileMetadataProvider fileMetadataProvider;
	private final FileMetadataToDocumentConverter toDocumentConverter;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;

	public FilesIndexReaderTemplate createFilesIndexReaderTemplate(Path indexPath) {
		return new FilesIndexReaderTemplate(toFileMetadataConverter,
				luceneFactories.createDocumentIndexReaderTemplate(indexPath));
	}

	public FSLuceneIndex createFilesIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(), indexPath);
	}

	public DocumentsDatasource createDocumentsDatasource() {
		return new DefaultDocumentsDatasource<>(fileMetadataProvider,
				RawDataIdToStringConverter.of(Path::toString),
				StringToRawDataIdConverter.of(Path::of),
				RawDataToDocumentConverter.of(toDocumentConverter::convert));
	}
}
