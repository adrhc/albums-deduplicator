package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.FileMetadataToDocumentConverter;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.persistence.lucene.write.DocumentIndexWriterTemplate;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static ro.go.adrhc.deduplicator.lib.LuceneFactories.standardTokenizer;
import static ro.go.adrhc.persistence.lucene.write.DocumentIndexWriterTemplate.fsWriterTemplate;

@TestConfiguration
@RequiredArgsConstructor
public class LuceneTestConfiguration {
	private final AppPaths appPaths;
	private final FilesIndexProperties indexProperties;

	@Bean
	public LuceneTokenizer luceneTokenizer() {
		return standardTokenizer(indexProperties);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FileMetadataToDocumentConverter createAudioMetadataToDocumentConverter() {
		return new FileMetadataToDocumentConverter(luceneTokenizer());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public DocumentIndexWriterTemplate indexWriterTemplate() {
		return fsWriterTemplate(luceneTokenizer().analyzer(), appPaths.getIndexPath());
	}
}
