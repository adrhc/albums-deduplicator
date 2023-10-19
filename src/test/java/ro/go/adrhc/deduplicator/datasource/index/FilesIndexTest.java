package ro.go.adrhc.deduplicator.datasource.index;

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
import ro.go.adrhc.deduplicator.stub.AppPathsGenerator;
import ro.go.adrhc.deduplicator.stub.DocumentGenerator;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.persistence.lucene.write.DocumentIndexWriterTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest(classes = LuceneTestConfiguration.class,
		properties = "lucene.search.result-includes-missing-files=true")
@ExcludeShellAutoConfiguration
@MockBean(classes = {Shell.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilesIndexTest {
	@Autowired
	FilesIndexProperties indexProperties;
	@Autowired
	LuceneTokenizer luceneTokenizer;
	@Autowired
	private ApplicationContext ac;
	@Autowired
	private AppPaths appPaths;
	@Autowired
	private DocumentGenerator documentGenerator;

	@Test
	void createIndexAddDocAndSearch(@TempDir Path tempDir) throws IOException {
		// index creation
		AppPathsGenerator.populateTestPaths(tempDir, appPaths);
		FilesIndex metadataIndex = filesMetadataIndex();
		metadataIndex.createOrReplaceIndex();

		Files.createFile(appPaths.getFilesPath().resolve("some-file.jpg"));
		metadataIndex.update();

//		// document adding
//		Document document = documentGenerator.create();
//		DocumentIndexWriterTemplate indexWriterTemplate = indexWriterTemplate();
//		indexWriterTemplate.useWriter(writer -> writer.addDocument(document));
//
//		// search
//		FoundAudio<SearchedAudio, DiskLocation> foundAudio = diskFoundAudio();
//		List<FoundAudio<SearchedAudio, DiskLocation>> result = diskLocationsIndexSearcher()
//				.findAllMatches(foundAudio.searchedAudio());
//
//		assertThat(result).hasSize(1);
//		assertEquals(foundAudio, result.get(0));
	}

//	private DiskLocationsIndexSearcher<SearchedAudio> diskLocationsIndexSearcher() {
//		return ac.getBean(DiskLocationsIndexSearcher.class); // SCOPE_PROTOTYPE
//	}

	private FilesIndex filesMetadataIndex() {
		return ac.getBean(FilesIndex.class); // SCOPE_PROTOTYPE
	}

	private DocumentIndexWriterTemplate indexWriterTemplate() {
		return ac.getBean(DocumentIndexWriterTemplate.class); // SCOPE_PROTOTYPE
	}
}
