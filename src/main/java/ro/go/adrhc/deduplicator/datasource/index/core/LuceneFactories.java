package ro.go.adrhc.deduplicator.datasource.index.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.persistence.lucene.index.core.analysis.AnalyzerFactory;
import ro.go.adrhc.persistence.lucene.index.core.read.DocumentIndexReaderTemplate;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class LuceneFactories {
	private final FilesIndexProperties indexProperties;

	public AnalyzerFactory createAnalyzerFactory() {
		return new AnalyzerFactory(indexProperties.getTokenizer());
	}

	public DocumentIndexReaderTemplate createDocumentIndexReaderTemplate(Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}
}
