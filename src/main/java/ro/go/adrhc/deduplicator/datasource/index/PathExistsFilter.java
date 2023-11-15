package ro.go.adrhc.deduplicator.datasource.index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.metadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedcore.read.ScoreAndValue;
import ro.go.adrhc.persistence.lucene.typedindex.search.SearchResultFilter;
import ro.go.adrhc.util.io.FileSystemUtils;

@Component
@RequiredArgsConstructor
public class PathExistsFilter implements SearchResultFilter<FileMetadata> {
	private final FileSystemUtils fsUtils;

	@Override
	public boolean filter(ScoreAndValue<FileMetadata> result) {
		return fsUtils.exists(result.value().path());
	}
}
