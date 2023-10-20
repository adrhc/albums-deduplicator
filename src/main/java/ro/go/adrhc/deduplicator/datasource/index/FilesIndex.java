package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.MetadataProvider;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.persistence.lucene.FSLuceneIndex;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReader;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static ro.go.adrhc.util.EnumUtils.toNamesSet;
import static ro.go.adrhc.util.fn.SneakyBiFunctionUtils.curry;

@RequiredArgsConstructor
@Slf4j
public class FilesIndex<MID, M> {
	private final String idField;
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final MetadataProvider<MID, M> metadataProvider;
	private final Function<String, MID> metadataIdParser;
	private final DocumentIndexReaderTemplate indexReaderTemplate;
	private final FSLuceneIndex<M> luceneIndex;

	public FileMetadataDuplicates findDuplicates() throws IOException {
		return indexReaderTemplate.useReader(this::doFind);
	}

	private FileMetadataDuplicates doFind(DocumentIndexReader indexReader) {
		Stream<FileMetadata> metadataStream = indexReader
				.getAll(toNamesSet(IndexFieldType.class))
				.map(toFileMetadataConverter::convert);
		return FileMetadataDuplicates.of(metadataStream);
	}

	public void createOrReplace() throws IOException {
		luceneIndex.createOrReplace(metadataProvider.loadAll());
	}

	public void update() throws IOException {
		IndexChanges<MID> changes = getIndexChanges();
		if (changes.hasChanges()) {
			applyIndexChanges(changes);
		} else {
			log.debug("\nNo changes detected!");
		}
	}

	private IndexChanges<MID> getIndexChanges() throws IOException {
		return indexReaderTemplate.transformFieldStream(idField,
				curry(this::transformFieldStream, metadataProvider.loadAllIds()));
	}

	private IndexChanges<MID> transformFieldStream(List<MID> paths, Stream<String> fieldStream) {
		List<String> docsToRemove = fieldStream.filter(id -> !paths.remove(metadataIdParser.apply(id))).toList();
		return new IndexChanges<>(paths, docsToRemove);
	}

	private void applyIndexChanges(IndexChanges<MID> changes) throws IOException {
		log.debug("\nremoving {} missing songs from the index", changes.indexIdsMissingActualDataSize());
		luceneIndex.removeByIds(changes.indexIdsMissingActualData());
		log.debug("\nloading {} new songs metadata", changes.notIndexedActualDataSize());
		Collection<M> fileMetadata = metadataProvider
				.loadByIds(changes.notIndexedActualDataCollection());
		log.debug("\nadding {} metadata records to the index", fileMetadata.size());
		luceneIndex.addItems(fileMetadata);
	}
}
