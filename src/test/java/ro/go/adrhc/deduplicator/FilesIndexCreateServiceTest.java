package ro.go.adrhc.deduplicator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Shell;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableAppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.deduplicator.services.FileMetadataCopies;
import ro.go.adrhc.deduplicator.services.FileMetadataCopiesCollection;
import ro.go.adrhc.deduplicator.services.FilesDedupService;
import ro.go.adrhc.deduplicator.stub.AppPathsGenerator;
import ro.go.adrhc.deduplicator.stub.FileGenerator;
import ro.go.adrhc.deduplicator.stub.ImageFileSpecification;
import ro.go.adrhc.persistence.lucene.typedindex.IndexRepository;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ro.go.adrhc.deduplicator.stub.ImageFileSpecification.of1024;
import static ro.go.adrhc.deduplicator.stub.ImageFileSpecification.of512;

@SpringBootTest(properties = "lucene.search.result-includes-missing-files=true")
@ExcludeShellAutoConfiguration
@MockBean(classes = {Shell.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FilesIndexCreateServiceTest {
	@Autowired
	private ApplicationContext ac;
	@Autowired
	private ObservableAppPaths observableAppPaths;
	@Autowired
	private IndexDataSource<Path, FileMetadata> indexDataSource;
	@Autowired
	private FileGenerator fileGenerator;

	@Test
	void findDuplicates(@TempDir Path tempDir) throws IOException {
		AppPathsGenerator.populateTestPaths(tempDir, observableAppPaths);

		initializeIndex(of512("1st-file.jpg"),
				of512("2nd-file.jpg"), of1024("3rd-file.jpg"));

		FileMetadataCopiesCollection duplicates = filesDedupService().find();
		log.debug("\n{}", duplicates);
		assertThat(duplicates.count()).isEqualTo(1);
		assertThat(duplicates.stream().map(FileMetadataCopies::getDuplicates)
				.flatMap(Set::stream).map(FileMetadata::fileNameNoExt))
				.containsOnly("2nd-file", "3rd-file");
	}

	private void initializeIndex(ImageFileSpecification... specifications) throws IOException {
		indexRepository().initialize(indexDataSource.loadAll());
		fileGenerator.createImageFiles(specifications);
		indexRepository().restore(indexDataSource);
	}

	private FilesDedupService filesDedupService() {
		return ac.getBean(FilesDedupService.class); // SCOPE_PROTOTYPE
	}

	private IndexRepository<Path, FileMetadata> indexRepository() {
		return ac.getBean(IndexRepository.class); // SCOPE_PROTOTYPE
	}
}
