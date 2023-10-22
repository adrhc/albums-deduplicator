package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.persistence.lucene.index.core.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class LuceneFactories {
	private final FilesIndexProperties indexProperties;

	public LuceneTokenizer createLuceneTokenizer() {
		return LuceneTokenizer.standardTokenizer(indexProperties.getTokenizer());
	}

	public DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}
}
