package ro.go.adrhc.deduplicator.datasource.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.core.field.FieldType;
import ro.go.adrhc.persistence.lucene.typedcore.field.TypedField;
import ro.go.adrhc.persistence.lucene.typedcore.field.TypedFieldSerde;

import static ro.go.adrhc.persistence.lucene.core.field.FieldType.*;
import static ro.go.adrhc.persistence.lucene.typedcore.field.TypedFieldSerde.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FileMetadataFieldType implements TypedField<FileMetadata> {
	filePath(KEYWORD, pathToString(FileMetadata::getId), true),
	fileNameNoExt(PHRASE, stringField(FileMetadata::fileNameNoExt)),
	lastModified(KEYWORD, stringField(FileMetadata::lastModifiedAsString)),
	size(LONG, longField(FileMetadata::size)),
	fileHash(KEYWORD, stringField(FileMetadata::fileHash));

	private final FieldType fieldType;
	private final TypedFieldSerde<FileMetadata> fieldSerde;
	private final boolean isIdField;

	FileMetadataFieldType(FieldType fieldType, TypedFieldSerde<FileMetadata> fieldSerde) {
		this.fieldType = fieldType;
		this.fieldSerde = fieldSerde;
		this.isIdField = false;
	}
}
