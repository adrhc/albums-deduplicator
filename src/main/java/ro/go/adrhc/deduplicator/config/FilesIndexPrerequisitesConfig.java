package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProviderFactory;
import ro.go.adrhc.deduplicator.lib.LuceneTokenizerFactory;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;

@Configuration
@RequiredArgsConstructor
public class FilesIndexPrerequisitesConfig {
	private final LuceneTokenizerFactory luceneTokenizerFactory;
	private final FileMetadataProviderFactory fileMetadataProviderFactory;

	@Bean
	public LuceneTokenizer standardTokenizer() {
		return luceneTokenizerFactory.create();
	}

	@Bean
	public FileMetadataProvider fileMetadataProvider() {
		return fileMetadataProviderFactory.create();
	}
}
