package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataFactory;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndex;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndexFactory;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.util.concurrent.ExecutorService;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FilesIndexConfig {
	private final AppPaths appPaths;
	private final AppProperties appProperties;
	private final ExecutorService metadataExecutorService;
	private final FilesIndexProperties indexProperties;
	private final FileSystemUtils fsUtils;
	private final FileMetadataFactory metadataFactory;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public FilesIndex audioFilesMetadataIndex() {
		return filesIndexFactory().create(appPaths.getIndexPath());
	}

	@Bean
	public FilesIndexFactory filesIndexFactory() {
		return new FilesIndexFactory(indexProperties, toFileMetadataConverter,
				luceneTokenizer(), filesDirectory(), fileMetadataProvider());
	}

	/**
	 * This (singleton) works well only if StandardAnalyzer is thread safe!
	 */
	@Bean
	public LuceneTokenizer luceneTokenizer() {
		return new LuceneTokenizer(new StandardAnalyzer(), indexProperties.getTokenizer());
	}

	@Bean
	public SimpleDirectory filesDirectory() {
		return SimpleDirectory.of(fsUtils, appPaths::getFilesPath,
				appProperties.getSupportedExtensions()::supports);
	}

	@Bean
	public FileMetadataProvider fileMetadataProvider() {
		return new FileMetadataProvider(metadataExecutorService, filesDirectory(), metadataFactory);
	}
}
