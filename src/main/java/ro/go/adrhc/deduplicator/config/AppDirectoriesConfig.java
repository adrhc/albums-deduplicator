package ro.go.adrhc.deduplicator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.deduplicator.datasource.AppDirectoryFactories;
import ro.go.adrhc.util.io.SimpleDirectory;

@Configuration
@RequiredArgsConstructor
public class AppDirectoriesConfig {
	private final AppDirectoryFactories appDirectoryFactories;

	@Bean
	public SimpleDirectory duplicatesDirectory() {
		return appDirectoryFactories.duplicatesDirectory();
	}

	@Bean
	public SimpleDirectory filesDirectory() {
		return appDirectoryFactories.filesDirectory();
	}
}
