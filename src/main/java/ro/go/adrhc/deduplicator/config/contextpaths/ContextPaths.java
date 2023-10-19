package ro.go.adrhc.deduplicator.config.contextpaths;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static ro.go.adrhc.util.io.PathUtils.parentOf;

@ConfigurationProperties(prefix = "context-paths")
@Component
@Getter
@Setter
@Slf4j
@ToString
public class ContextPaths implements Cloneable {
	private Path indexPath;
	private Path filesPath;

	public Path getIndexPathParent() {
		return parentOf(indexPath).orElse(null);
	}

	public void copy(ContextPaths contextPaths) {
		update(contextPaths.indexPath, contextPaths.filesPath);
	}

	public void update(Path indexPath, Path filesPath) {
		this.indexPath = ObjectUtils.defaultIfNull(indexPath, this.indexPath);
		this.filesPath = ObjectUtils.defaultIfNull(filesPath, this.filesPath);
	}

	@Override
	public ContextPaths clone() {
		try {
			return (ContextPaths) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
