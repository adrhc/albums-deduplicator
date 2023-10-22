package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.lib.LuceneTokenizerFactory;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;

@Configuration
@RequiredArgsConstructor
public class FilesIndexPrerequisitesConfig {
	private final LuceneTokenizerFactory luceneTokenizerFactory;

	@Bean
	public LuceneTokenizer standardTokenizer() {
		return luceneTokenizerFactory.create();
	}
}
