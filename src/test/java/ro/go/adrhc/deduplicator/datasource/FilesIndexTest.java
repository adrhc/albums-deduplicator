package ro.go.adrhc.deduplicator.datasource;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Shell;
import ro.go.adrhc.deduplicator.ExcludeShellAutoConfiguration;
import ro.go.adrhc.deduplicator.config.LuceneTestConfiguration;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.index.FilesIndex;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.deduplicator.stub.AppPathsGenerator;
import ro.go.adrhc.deduplicator.stub.FileGenerator;
import ro.go.adrhc.deduplicator.stub.ImageFileSpecification;

import java.io.IOException;
import java.nio.file.Path;

import static ro.go.adrhc.deduplicator.stub.ImageFileSpecification.of;

@SpringBootTest(classes = LuceneTestConfiguration.class,
		properties = "lucene.search.result-includes-missing-files=true")
@ExcludeShellAutoConfiguration
@MockBean(classes = {Shell.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FilesIndexTest {
	@Autowired
	private ApplicationContext ac;
	@Autowired
	private AppPaths appPaths;
	@Autowired
	private FileGenerator fileGenerator;

	@Test
	void findDuplicates(@TempDir Path tempDir) throws IOException {
		AppPathsGenerator.populateTestPaths(tempDir, appPaths);

		FilesIndex metadataIndex = createAndPopulate(of("1sr-file.jpg"),
				of("2nd-file.jpg"), new ImageFileSpecification("3rd-file.jpg", 512));

		FileMetadataDuplicates duplicates = metadataIndex.findDuplicates();
		log.debug("\n{}", duplicates);
	}

	private FilesIndex createAndPopulate(ImageFileSpecification... specifications) throws IOException {
		FilesIndex metadataIndex = filesMetadataIndex();
		metadataIndex.createOrReplace();
		fileGenerator.createImageFiles(specifications);
		metadataIndex.update();
		return metadataIndex;
	}

	private FilesIndex filesMetadataIndex() {
		return ac.getBean(FilesIndex.class); // SCOPE_PROTOTYPE
	}
}
