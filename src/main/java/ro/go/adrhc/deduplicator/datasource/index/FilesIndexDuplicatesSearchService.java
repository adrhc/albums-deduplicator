package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.index.dedup.DocumentToFileMetadataConverter;
import ro.go.adrhc.deduplicator.datasource.index.dedup.FileMetadataDuplicates;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReader;
import ro.go.adrhc.persistence.lucene.read.DocumentIndexReaderTemplate;

import java.io.IOException;
import java.util.stream.Stream;

import static ro.go.adrhc.util.EnumUtils.toNamesSet;

@RequiredArgsConstructor
@Slf4j
public class FilesIndexDuplicatesSearchService {
	private final DocumentToFileMetadataConverter toFileMetadataConverter;
	private final DocumentIndexReaderTemplate indexReaderTemplate;

	public FileMetadataDuplicates find() throws IOException {
		return indexReaderTemplate.useReader(this::doFind);
	}

	private FileMetadataDuplicates doFind(DocumentIndexReader indexReader) {
		Stream<FileMetadata> metadataStream = indexReader
				.getAll(toNamesSet(IndexFieldType.class))
				.map(toFileMetadataConverter::convert);
		return FileMetadataDuplicates.of(metadataStream);
	}
}
