package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.util.io.FileSystemUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {
	private final AppProperties appProperties;

	@Bean
	public FileSystemUtils fileSystemUtils() {
		return new FileSystemUtils();
	}

	@Bean
	public ExecutorService metadataExecutorService() {
		return Executors.newFixedThreadPool(appProperties.getMetadataLoadingThreads());
	}
}
