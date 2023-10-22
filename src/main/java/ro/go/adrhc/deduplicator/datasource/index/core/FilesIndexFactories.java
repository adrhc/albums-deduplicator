package ro.go.adrhc.deduplicator.datasource.index.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.LuceneFactories;
import ro.go.adrhc.deduplicator.datasource.index.domain.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.domain.IndexFieldType;
import ro.go.adrhc.persistence.lucene.fsindex.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;

import java.nio.file.Path;

import static ro.go.adrhc.persistence.lucene.fsindex.LuceneIndexFactories.createFSIndex;

@Component
@RequiredArgsConstructor
public class FilesIndexFactories {
	private final LuceneFactories luceneFactories;
	private final LuceneTokenizer luceneTokenizer;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;

	public FilesIndexReaderTemplate createFilesIndexReaderTemplate(Path indexPath) {
		return new FilesIndexReaderTemplate(toFileMetadataConverter,
				luceneFactories.createDocumentIndexReaderTemplate(indexPath));
	}

	public FSLuceneIndex createFilesIndex(Path indexPath) {
		return createFSIndex(IndexFieldType.filePath, luceneTokenizer.analyzer(), indexPath);
	}
}
