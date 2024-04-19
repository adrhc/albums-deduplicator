package ro.go.adrhc.deduplicator.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ro.go.adrhc.deduplicator.config.apppaths.ObservableAppPaths;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.restore.IndexDataSource;
import ro.go.adrhc.util.StopWatchUtils;

import java.io.IOException;
import java.nio.file.Path;

@ShellComponent("Files index management.")
@Slf4j
public class IndexCommands extends AbstractCommand {
    public IndexCommands(ApplicationContext ac, ObservableAppPaths appPaths,
            IndexDataSource<Path, FileMetadata> indexDataSource) {
        super(ac, appPaths, indexDataSource);
    }

    @ShellMethod(key = {"create", "reset"},
            value = "Create the index at the provided path (remove it first, if exists).")
    public void reset() throws IOException {
        StopWatch watch = StopWatchUtils.start();
        fileMetadataRepository().reset(indexDataSource.loadAll());
        watch.stop();
        log.debug("\n{} index created!\nIndexed {} songs in {}.",
                appPaths.getIndexPath(), fileMetadataRepository().count(), watch.formatTime());
    }

    @ShellMethod("Get the index size.")
    public void count() throws IOException {
        log.debug("\nindex size is {}", fileMetadataRepository().count());
    }

    @ShellMethod(value = "Update the index at the provided path.", key = {"update", "reindex"})
    public void update() {
        try {
            fileMetadataRepository().restore(indexDataSource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.debug("\n{} index update failed!", appPaths.getIndexPath());
        }
    }
}
