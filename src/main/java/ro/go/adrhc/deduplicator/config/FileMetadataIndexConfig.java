package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ro.go.adrhc.deduplicator.datasource.index.ScopedIndexRepository;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;

import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class FileMetadataIndexConfig {
	private final ScopedIndexRepository scopedIndexRepository;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public IndexRepository<Path, FileMetadata> fileMetadataRepository() throws IOException {
		return scopedIndexRepository.getIndexRepository();
	}
}
