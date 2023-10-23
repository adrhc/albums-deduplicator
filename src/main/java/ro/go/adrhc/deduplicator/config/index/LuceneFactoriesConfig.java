package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.index.core.LuceneFactories;
import ro.go.adrhc.persistence.lucene.index.core.tokenizer.LuceneTokenizer;

@Configuration
@RequiredArgsConstructor
public class LuceneFactoriesConfig {
	private final LuceneFactories luceneFactories;

	@Bean
	public LuceneTokenizer standardTokenizer() {
		return luceneFactories.createLuceneTokenizer();
	}
}
