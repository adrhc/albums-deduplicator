package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadataProvider;
import ro.go.adrhc.persistence.lucene.IndexAdmin;
import ro.go.adrhc.persistence.lucene.IndexUpdater;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class FilesIndex {
	private static final String ID_FIELD = IndexFieldType.filePath.name();
	private final SimpleDirectory filesDirectory;
	private final FileMetadataProvider metadataProvider;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final IndexAdmin<FileMetadata> indexAdmin;
	private final IndexUpdater<FileMetadata> indexUpdater;

	public void createOrReplaceIndex() throws IOException {
		indexAdmin.createOrReplaceIndex(metadataProvider.loadAllMetadata());
	}

	public void update() throws IOException {
		IndexChanges changes = identifyIndexChanges();
		if (changes.hasChanges()) {
			applyIndexChanges(changes);
		} else {
			log.debug("\nNo changes detected!");
		}
	}

	private IndexChanges identifyIndexChanges() throws IOException {
		List<Path> existingFiles = new ArrayList<>(filesDirectory.getAllPaths());
		List<String> docsToRemove = searchAllIds().stream()
				.filter(indexed -> !existingFiles.remove(Path.of(indexed)))
				.toList();
		return new IndexChanges(existingFiles, docsToRemove);
	}

	private List<String> searchAllIds() throws IOException {
		return indexReaderTemplate.useReader(
				indexReader -> indexReader
						.getAll(Set.of(ID_FIELD))
						.stream()
						.map(doc -> doc.get(ID_FIELD))
						.toList());
	}

	private void applyIndexChanges(IndexChanges changes) throws IOException {
		log.debug("\nremoving {} missing songs from the index", changes.fsMissingIndexedPaths().size());
		indexUpdater.removeByIds(changes.fsMissingIndexedPaths());
		log.debug("\nloading {} new songs metadata", changes.fsPresentNotIndexedPaths().size());
		Collection<FileMetadata> songsMetadata = metadataProvider.loadMetadata(changes.fsPresentNotIndexedPaths());
		log.debug("\nadding {} metadata records to the index", songsMetadata.size());
		indexUpdater.addItems(songsMetadata);
	}
}
