package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.index.core.FilesIndexFactories;
import ro.go.adrhc.persistence.lucene.index.spi.DocumentsDatasource;

@Configuration
@RequiredArgsConstructor
public class FilesIndexFactoriesConfig {
	private final FilesIndexFactories filesIndexFactories;

	@Bean
	public DocumentsDatasource createDocumentsDatasource() {
		return filesIndexFactories.createDocumentsDatasource();
	}
}
