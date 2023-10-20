package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChanges;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChangesProvider;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.persistence.lucene.IndexAdmin;
import ro.go.adrhc.persistence.lucene.IndexUpdater;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReader;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.util.EnumUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
public class FilesIndex {
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final FileMetadataProvider metadataProvider;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final IndexUpdater<FileMetadata> indexUpdater;
	private final IndexAdmin<FileMetadata> indexAdmin;
	private final IndexChangesProvider<Path> indexChangesProvider;

	public void createOrReplaceIndex() throws IOException {
		indexAdmin.createOrReplaceIndex(metadataProvider.loadAllMetadata());
	}

	public void update() throws IOException {
		IndexChanges<Path> changes = indexChangesProvider.getChanges();
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

	private void applyIndexChanges(IndexChanges<Path> changes) throws IOException {
		log.debug("\nremoving {} missing songs from the index", changes.indexIdsMissingActualDataSize());
		indexUpdater.removeByIds(changes.indexIdsMissingActualData());
		log.debug("\nloading {} new songs metadata", changes.notIndexedActualDataSize());
		Collection<FileMetadata> songsMetadata = metadataProvider
				.loadMetadata(changes.notIndexedActualDataCollection());
		log.debug("\nadding {} metadata records to the index", songsMetadata.size());
		indexUpdater.addItems(songsMetadata);
	}
}
