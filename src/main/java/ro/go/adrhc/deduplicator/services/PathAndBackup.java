package ro.go.adrhc.deduplicator.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
@Getter
public class PathAndBackup {
    private final Path path;
    private final Path backupPath;

    @Override
    public String toString() {
        return "%s -> %s".formatted(path, backupPath);
    }
}
