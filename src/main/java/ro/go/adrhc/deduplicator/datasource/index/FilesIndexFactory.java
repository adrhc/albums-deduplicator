package ro.go.adrhc.deduplicator.datasource.index;

import com.rainerhahnekamp.sneakythrow.functional.SneakyFunction;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChangesProvider;
import ro.go.adrhc.deduplicator.datasource.index.config.FilesIndexProperties;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.lib.LuceneFactories;
import ro.go.adrhc.persistence.lucene.IndexAdmin;
import ro.go.adrhc.persistence.lucene.IndexUpdater;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.persistence.lucene.tokenizer.LuceneTokenizer;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static ro.go.adrhc.deduplicator.datasource.index.changes.DefaultActualData.actualPaths;

@RequiredArgsConstructor
public class FilesIndexFactory {
	private final FilesIndexProperties indexProperties;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final LuceneTokenizer luceneTokenizer;
	private final SimpleDirectory filesDirectory;
	private final FileMetadataProvider metadataProvider;

	public FilesIndex create(Path indexPath) {
		return new FilesIndex(
				toFileMetadataConverter, metadataProvider,
				indexReaderTemplate(indexPath),
				createIndexAdmin(indexPath),
				createIndexUpdater(indexPath),
				fsIndexChangesProvider(indexPath));
	}

	private IndexChangesProvider<Path> fsIndexChangesProvider(Path indexPath) {
		return new IndexChangesProvider<>(IndexFieldType.filePath.name(),
				() -> actualPaths(filesDirectory::getAllPaths),
				LuceneFactories.create(indexProperties, indexPath));
	}

	private FileMetadataToDocumentConverter createAudioMetadataToDocumentConverter() {
		return new FileMetadataToDocumentConverter(luceneTokenizer);
	}

	private DocumentIndexReaderTemplate indexReaderTemplate(Path indexPath) {
		return LuceneFactories.create(indexProperties, indexPath);
	}

	private IndexAdmin<FileMetadata> createIndexAdmin(Path indexPath) {
		return IndexAdmin.create(indexPath, luceneTokenizer, toDocumentConverter());
	}

	private IndexUpdater<FileMetadata> createIndexUpdater(Path indexPath) {
		return IndexUpdater.create(IndexFieldType.filePath,
				indexPath, luceneTokenizer, toDocumentConverter());
	}

	private SneakyFunction<FileMetadata, Optional<Document>, IOException> toDocumentConverter() {
		return am -> Optional.of(createAudioMetadataToDocumentConverter().convert(am));
	}
}
