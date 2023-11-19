package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.boot.NonInteractiveShellRunnerCustomizer;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadataLoader;
import ro.go.adrhc.util.concurrency.CompletableFuturesToOutcomeStreamConverter;
import ro.go.adrhc.util.io.FileSystemUtils;
import ro.go.adrhc.util.io.FilesMetadataLoader;
import ro.go.adrhc.util.io.SimpleDirectory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {
	private final AppProperties appProperties;

	@Bean
	public ExecutorService adminExecutorService() {
		return Executors.newCachedThreadPool();
	}

	@Bean
	public FilesMetadataLoader<Optional<FileMetadata>> filesMetadataLoader(
			SimpleDirectory filesDirectory, FileMetadataLoader metadataFactory) {
		return new FilesMetadataLoader<>(metadataExecutorService(),
				futuresToStreamConverter(), filesDirectory, metadataFactory::load);
	}

	@Bean
	public ExecutorService metadataExecutorService() {
		return Executors.newFixedThreadPool(appProperties.getMetadataLoadingThreads());
	}

	@Bean
	public CompletableFuturesToOutcomeStreamConverter futuresToStreamConverter() {
		return new CompletableFuturesToOutcomeStreamConverter(adminExecutorService());
	}

	@Bean
	public FileSystemUtils fileSystemUtils() {
		return new FileSystemUtils();
	}

	@Bean
	public NonInteractiveShellRunnerCustomizer nonInteractiveShellRunnerCustomizer() {
		return shellRunner -> shellRunner.setCommandsFromInputArgs(this::skipSpringBootParams);
	}

	private List<String> skipSpringBootParams(ApplicationArguments appArgs) {
		String command = Stream.of(appArgs.getSourceArgs())
				.filter(arg -> !arg.startsWith("--spring."))
				.collect(Collectors.joining(" "));
		return command.isBlank() ? List.of() : Collections.singletonList(command);
	}
}
