package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.index.core.LuceneFactories;
import ro.go.adrhc.persistence.lucene.index.core.analysis.AnalyzerFactory;

@Configuration
@RequiredArgsConstructor
public class LuceneFactoriesConfig {
	private final LuceneFactories luceneFactories;

	@Bean
	public Analyzer analyzer() {
		return analyzerFactory().create();
	}

	@Bean
	public AnalyzerFactory analyzerFactory() {
		return luceneFactories.createAnalyzerFactory();
	}
}
