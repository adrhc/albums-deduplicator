package ro.go.adrhc.deduplicator.datasource.index.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.index.domain.field.FieldType;
import ro.go.adrhc.persistence.lucene.typedindex.domain.field.TypedFieldEnum;

import java.util.function.Function;

import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldType.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FileMetadataFieldType implements TypedFieldEnum<FileMetadata> {
	filePath(KEYWORD, FileMetadata::path),
	fileNameNoExt(PHRASE, FileMetadata::fileNameNoExt),
	lastModified(KEYWORD, FileMetadata::lastModifiedAsString),
	size(LONG, FileMetadata::size),
	fileHash(KEYWORD, FileMetadata::fileHash);

	private final FieldType fieldType;
	private final Function<FileMetadata, Object> accessor;
}
