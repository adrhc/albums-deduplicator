package ro.go.adrhc.deduplicator.datasource.index.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.go.adrhc.deduplicator.datasource.filesmetadata.FileMetadata;
import ro.go.adrhc.persistence.lucene.typedindex.domain.seach.QuerySearchResult;
import ro.go.adrhc.persistence.lucene.typedindex.search.QuerySearchResultFilter;
import ro.go.adrhc.util.io.FileSystemUtils;

@Component
@RequiredArgsConstructor
public class PathExistsFilter implements QuerySearchResultFilter<FileMetadata> {
	private final FileSystemUtils fsUtils;

	@Override
	public boolean filter(QuerySearchResult<FileMetadata> result) {
		return fsUtils.exists(result.getFound().path());
	}
}
