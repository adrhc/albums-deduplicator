package ro.go.adrhc.deduplicator.services;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ro.go.adrhc.util.text.StringUtils.concat;

@RequiredArgsConstructor
public class OriginalAndDupBackups implements Iterable<PathAndBackup> {
	private final PathAndBackup originalBackup;
	private final List<PathAndBackup> dupBackups;

	public static OriginalAndDupBackups of(PathAndBackup originalBackup) {
		return new OriginalAndDupBackups(originalBackup, new ArrayList<>());
	}

	public void add(PathAndBackup pathAndBackup) {
		dupBackups.add(pathAndBackup);
	}

	public Path getOriginalPath() {
		return originalBackup.getPath();
	}

	public Path getOriginalBackupPath() {
		return originalBackup.getBackupPath();
	}

	@Override
	public String toString() {
		return originalBackup.toString() + '\n' + concat(dupBackups.stream());
	}

	@Override
	public Iterator<PathAndBackup> iterator() {
		return dupBackups.iterator();
	}
}
