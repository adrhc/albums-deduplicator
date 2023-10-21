package ro.go.adrhc.deduplicator.lib;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.persistence.lucene.domain.tokenizer.LuceneTokenizer;

@Component
@RequiredArgsConstructor
public class LuceneTokenizerFactory {
	private final FilesIndexProperties indexProperties;

	public LuceneTokenizer create() {
		return LuceneTokenizer.standardTokenizer(indexProperties.getTokenizer());
	}
}
