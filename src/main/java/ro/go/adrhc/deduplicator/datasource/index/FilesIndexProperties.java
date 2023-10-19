package ro.go.adrhc.deduplicator.datasource.index;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ro.go.adrhc.persistence.lucene.tokenizer.TokenizerProperties;

@ConfigurationProperties(prefix = "lucene")
@Component
@Setter
@Getter
@ToString
public class FilesIndexProperties {
	private TokenizerProperties tokenizer;
	private QueryProperties query;
	private SearchProperties search;
}
