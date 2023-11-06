package ro.go.adrhc.deduplicator.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Shell;
import ro.go.adrhc.deduplicator.ExcludeShellAutoConfiguration;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FileMetadataCopies;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FileMetadataCopiesCollection;
import ro.go.adrhc.deduplicator.datasource.index.services.dedup.FilesIndexDedupService;
import ro.go.adrhc.deduplicator.stub.AppPathsGenerator;
import ro.go.adrhc.deduplicator.stub.FileGenerator;
import ro.go.adrhc.deduplicator.stub.ImageFileSpecification;
import ro.go.adrhc.persistence.lucene.typedindex.TypedIndexCreateService;
import ro.go.adrhc.persistence.lucene.typedindex.restore.DocumentsIndexRestoreService;
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
	private AppPaths appPaths;
	@Autowired
	private FileMetadataProvider fileMetadataProvider;
	@Autowired
	private IndexDataSource<String, Document> indexDataSource;
	@Autowired
	private FileGenerator fileGenerator;

	@Test
	void findDuplicates(@TempDir Path tempDir) throws IOException {
		AppPathsGenerator.populateTestPaths(tempDir, appPaths);

		createAndPopulate(of512("1st-file.jpg"),
				of512("2nd-file.jpg"), of1024("3rd-file.jpg"));

		FileMetadataCopiesCollection duplicates = filesIndexDuplicatesMngmtService().find();
		log.debug("\n{}", duplicates);
		assertThat(duplicates.count()).isEqualTo(1);
		assertThat(duplicates.stream().map(FileMetadataCopies::getDuplicates)
				.flatMap(Set::stream).map(FileMetadata::fileNameNoExt))
				.containsOnly("2nd-file", "3rd-file");
	}

	private void createAndPopulate(ImageFileSpecification... specifications) throws IOException {
		fsIndexCreateService().createOrReplace(fileMetadataProvider.loadAll());
		fileGenerator.createImageFiles(specifications);
		dsIndexRestoreService().restore(indexDataSource);
	}

	private TypedIndexCreateService<FileMetadata> fsIndexCreateService() {
		return ac.getBean(TypedIndexCreateService.class); // SCOPE_PROTOTYPE
	}

	private FilesIndexDedupService filesIndexDuplicatesMngmtService() {
		return ac.getBean(FilesIndexDedupService.class); // SCOPE_PROTOTYPE
	}

	private DocumentsIndexRestoreService<String, FileMetadata> dsIndexRestoreService() {
		return ac.getBean(DocumentsIndexRestoreService.class); // SCOPE_PROTOTYPE
	}
}
