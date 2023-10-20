package ro.go.adrhc.deduplicator.lib;

import lombok.experimental.UtilityClass;
import org.apache.lucene.search.FuzzyQuery;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

@UtilityClass
public class LuceneFactories {
	public static DocumentIndexReaderTemplate create(
			FilesIndexProperties indexProperties, Path indexPath) {
		return new DocumentIndexReaderTemplate(
				indexProperties.getSearch().getMaxResultsPerSearch(), indexPath);
	}

	public static LuceneTokenizer standardTokenizer(FilesIndexProperties indexProperties) {
		return LuceneTokenizer.standardTokenizer(indexProperties.getTokenizer());
	}

	public static Stream<FuzzyQuery> create(
			FilesIndexProperties indexProperties, String fieldName, Collection<String> tokens) {
		return ro.go.adrhc.persistence.lucene.queries.FuzzyQueryFactory.create(
				indexProperties.getQuery().getLevenshteinDistance(), fieldName, tokens);
	}
}
