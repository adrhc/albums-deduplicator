package ro.go.adrhc.deduplicator.datasource.index;

import com.rainerhahnekamp.sneakythrow.functional.SneakyFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.ActualData;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChanges;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReader;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.util.EnumUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import static ro.go.adrhc.deduplicator.datasource.index.changes.DefaultActualData.actualPaths;

@RequiredArgsConstructor
@Slf4j
public class FilesIndex {
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataProvider metadataProvider;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final FSLuceneIndex<FileMetadata> luceneIndex;
	private final SneakyFunction<ActualData<Path>, IndexChanges<Path>, IOException> indexChangesProvider;

	public void createOrReplace() throws IOException {
		luceneIndex.createOrReplace(metadataProvider.loadAllMetadata());
	}

	public void update() throws IOException {
		IndexChanges<Path> changes = getIndexChanges();
		if (changes.hasChanges()) {
			applyIndexChanges(changes);
		} else {
			log.debug("\nNo changes detected!");
		}
	}

	public FileMetadataDuplicates findDuplicates() throws IOException {
		return indexReaderTemplate.useReader(this::doFind);
	}

	private FileMetadataDuplicates doFind(DocumentIndexReader indexReader) {
		return indexReader.getAll(EnumUtils.toNamesSet(IndexFieldType.class))
				.map(toFileMetadataConverter::convert)
				.collect(FileMetadataDuplicates::create, FileMetadataDuplicates::add, FileMetadataDuplicates::addAll);
	}

	private IndexChanges<Path> getIndexChanges() throws IOException {
		return indexChangesProvider.apply(actualPaths(metadataProvider.loadAllPaths()));
	}

	private void applyIndexChanges(IndexChanges<Path> changes) throws IOException {
		log.debug("\nremoving {} missing songs from the index", changes.indexIdsMissingActualDataSize());
		luceneIndex.removeByIds(changes.indexIdsMissingActualData());
		log.debug("\nloading {} new songs metadata", changes.notIndexedActualDataSize());
		Collection<FileMetadata> fileMetadata = metadataProvider
				.loadMetadata(changes.notIndexedActualDataCollection());
		log.debug("\nadding {} metadata records to the index", fileMetadata.size());
		luceneIndex.addItems(fileMetadata);
	}
}
