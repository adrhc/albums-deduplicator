package ro.go.adrhc.deduplicator.datasource.index;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SearchProperties {
	private int maxResultsPerSearch;
	private boolean resultIncludesMissingFiles;
}
