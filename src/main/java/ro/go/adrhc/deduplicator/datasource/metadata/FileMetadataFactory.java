package ro.go.adrhc.deduplicator.datasource.metadata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.size;
import static ro.go.adrhc.util.io.FilenameUtils.filenameNoExt;

@Component
@Slf4j
public class FileMetadataFactory {
	public Optional<FileMetadata> create(Path path) {
		return hash(path).map(Base64::encodeBase64).map(String::new)
				.flatMap(hash -> toFileMetadata(path, hash));
	}

	private Optional<FileMetadata> toFileMetadata(Path path, String hash) {
		try {
			long size = size(path);
			FileTime lastModified = getLastModifiedTime(path);
			return filenameNoExt(path).map(filenameNoExt ->
					new FileMetadata(path, filenameNoExt,
							lastModified.toInstant(), hash, size));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	private Optional<byte[]> hash(Path path) {
		try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
			return Optional.of(DigestUtils.sha3_224(inputStream));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
	}
}
