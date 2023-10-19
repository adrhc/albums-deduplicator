package ro.go.adrhc.deduplicator.config;

import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Setter
@ConfigurationProperties
@ToString
public class AppProperties {
	private Set<String> supportedExtensions;
}
