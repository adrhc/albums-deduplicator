package ro.go.adrhc.deduplicator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.go.adrhc.util.io.FileSystemUtils;

@Configuration
public class AppConfiguration {
	@Bean
	public FileSystemUtils fileSystemUtils() {
		return new FileSystemUtils();
	}
}
