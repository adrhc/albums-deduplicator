package ro.go.adrhc.deduplicator.datasource.metadata;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.Shell;
import ro.go.adrhc.deduplicator.ExcludeShellAutoConfiguration;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.collection.StreamCounter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
@ExcludeShellAutoConfiguration
@MockBean(classes = {Shell.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FileMetadataProviderTest {
	@Autowired
	private IndexDataSource<Path, FileMetadata> indexDataSource;

	@Test
	void loadAll() throws IOException {
		Stream<FileMetadata> metadataStream = indexDataSource.loadAll();
		StreamCounter counter = new StreamCounter();
		counter.countedStream(metadataStream)
				.forEach(m -> log.info("\nprocessing done for: {}", m.fileNameNoExt()));
		assertThat(counter.getCount()).isPositive();
	}
}