package ro.go.adrhc.deduplicator.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ro.go.adrhc.util.io.SupportedExtensions;

import java.util.Set;

@Component
@ConfigurationProperties
@ToString
public class AppProperties {
	@Getter
	private SupportedExtensions supportedExtensions;

	public void setSupportedExtensions(Set<String> supportedExtensions) {
		this.supportedExtensions = new SupportedExtensions(supportedExtensions);
	}
}
