package ro.go.adrhc.deduplicator.config.index;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.domain.FileMetadataFieldType;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexFactories;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class TypedIndexFactoriesConfig {
	private final FilesIndexProperties indexProperties;

	@Bean
	public TypedIndexFactories<String, FileMetadata, FileMetadataFieldType> typedIndexFactories() throws IOException {
		return TypedIndexFactories.create(
				indexProperties.getSearch().getMaxResultsPerSearch(),
				FileMetadata.class, FileMetadataFieldType.class,
				indexProperties.getTokenizer());
	}
}
