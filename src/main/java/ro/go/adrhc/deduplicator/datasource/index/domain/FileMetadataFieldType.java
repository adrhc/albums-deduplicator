package ro.go.adrhc.deduplicator.datasource.index.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.index.IndexableField;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.index.domain.field.FieldType;
import ro.go.adrhc.persistence.lucene.typedindex.core.docds.rawds.Identifiable;
import ro.go.adrhc.persistence.lucene.typedindex.domain.field.TypedField;

import java.util.function.Function;

import static ro.go.adrhc.persistence.lucene.index.domain.field.FieldType.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FileMetadataFieldType implements TypedField<FileMetadata> {
	filePath(KEYWORD, Identifiable::getId, STRING_FIELD_ACCESSOR, true),
	fileNameNoExt(PHRASE, FileMetadata::fileNameNoExt),
	lastModified(KEYWORD, FileMetadata::lastModifiedAsString),
	size(LONG, FileMetadata::size),
	fileHash(KEYWORD, FileMetadata::fileHash);

	private final FieldType fieldType;
	private final Function<FileMetadata, ?> accessor;
	private final Function<IndexableField, Object> fieldValueAccessor;
	private final boolean isIdField;

	FileMetadataFieldType(FieldType fieldType, Function<FileMetadata, ?> accessor) {
		this.fieldType = fieldType;
		this.accessor = accessor;
		this.fieldValueAccessor = STRING_FIELD_ACCESSOR;
		this.isIdField = false;
	}

	FileMetadataFieldType(FieldType fieldType, Function<FileMetadata, ?> accessor,
			Function<IndexableField, Object> fieldValueAccessor) {
		this.fieldType = fieldType;
		this.accessor = accessor;
		this.fieldValueAccessor = fieldValueAccessor;
		this.isIdField = false;
	}
}
