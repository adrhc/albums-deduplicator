package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChanges;
import ro.go.adrhc.deduplicator.datasource.index.changes.IndexChangesProvider;
import ro.go.adrhc.persistence.lucene.IndexAdmin;
import ro.go.adrhc.persistence.lucene.IndexUpdater;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
public class FilesIndex {
	private final FileMetadataProvider metadataProvider;
	private final IndexAdmin<FileMetadata> indexAdmin;
	private final IndexUpdater<FileMetadata> indexUpdater;
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
