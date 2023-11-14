package ro.go.adrhc.deduplicator.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ro.go.adrhc.util.io.SupportedExtensions;

import java.util.Set;

import static ro.go.adrhc.util.CpuUtils.cpuCoresMultipliedBy;

@Component
@ConfigurationProperties
public class AppProperties {
	@Getter
	private SupportedExtensions supportedExtensions;
	@Getter
	private int metadataLoadingThreads = Runtime.getRuntime().availableProcessors();

	public void setSupportedExtensions(Set<String> supportedExtensions) {
		this.supportedExtensions = new SupportedExtensions(supportedExtensions);
	}

	public void setMetadataLoadingThreads(String metadataLoadingThreads) {
		int parsedMetadataLoadingThreads = metadataLoadingThreads.endsWith("c") ?
				cpuCoresMultipliedBy(metadataLoadingThreads) : Integer.parseInt(metadataLoadingThreads);
		this.metadataLoadingThreads = parsedMetadataLoadingThreads > 0 ?
				parsedMetadataLoadingThreads : Runtime.getRuntime().availableProcessors();
	}
}
