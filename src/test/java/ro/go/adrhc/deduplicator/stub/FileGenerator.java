package ro.go.adrhc.deduplicator.stub;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.config.apppaths.AppPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Component
public class FileGenerator {
	@Autowired
	private AppPaths appPaths;

	public void createImageFiles(ImageFileSpecification... specifications) throws IOException {
		for (ImageFileSpecification spec : specifications) {
			createImageFile(spec);
		}
	}

	public void createImageFile(String filename) throws IOException {
		createImageFile(ImageFileSpecification.of1024(filename));
	}

	public void createImageFile(ImageFileSpecification specification) throws IOException {
		Path path = Files.createFile(appPaths.getFilesPath().resolve(specification.filename()));
		byte[] content = new byte[specification.size()];
		Arrays.fill(content, (byte) 'a');
		try (FileWriter writer = new FileWriter(path.toFile())) {
			IOUtils.write(content, writer, StandardCharsets.UTF_8);
		}
	}
}
