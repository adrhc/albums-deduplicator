package ro.go.adrhc.deduplicator.config.apppaths;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static ro.go.adrhc.util.io.PathUtils.parentOf;

@ConfigurationProperties(prefix = "app-paths")
@Component
@Getter
@Setter
@Slf4j
@ToString
public class AppPaths {
    private Path indexPath;
    private Path filesPath;
    private Path duplicatesPath;

    public Path getIndexPathParent() {
        return parentOf(indexPath).orElse(null);
    }

    void update(Path indexPath, Path filesPath, Path duplicatesPath) {
        this.indexPath = ObjectUtils.defaultIfNull(indexPath, this.indexPath);
        this.filesPath = ObjectUtils.defaultIfNull(filesPath, this.filesPath);
        this.duplicatesPath = ObjectUtils.defaultIfNull(duplicatesPath, this.duplicatesPath);
    }
}
