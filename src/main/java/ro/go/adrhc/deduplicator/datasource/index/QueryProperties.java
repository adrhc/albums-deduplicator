package ro.go.adrhc.deduplicator.datasource.index;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class QueryProperties {
	private int toMatchExactlyMaxTokenLength;
	private int minimumWordToMatch;
	private float minimumWordToMatchWhenMissingDurationPart1;
	private float minimumWordToMatchWhenMissingDurationPart2;
	private int levenshteinDistance;

	public int minWordsToFind(int tokensAmount) {
		float configuredProportion = getWordsToMatchRatioWhenMissingDuration();
		int ratioBasedMinimum = Math.round(tokensAmount * configuredProportion);
		if (ratioBasedMinimum > minimumWordToMatch) {
			return ratioBasedMinimum;
		} else if (tokensAmount > minimumWordToMatch) {
			return tokensAmount - 1;
		} else {
			return tokensAmount;
		}
	}

	/**
	 * toMatchExactlyMaxTokenLength refers to tokens to match exactly
	 * while this method returns exactly that, i.e. tokens to match
	 * exactly but nothing more, hence it makes sens to stay here.
	 */
	public Set<String> matchExactlyTokens(Collection<String> tokenizedWords) {
		return tokenizedWords.stream()
				.filter(it -> it.length() <= toMatchExactlyMaxTokenLength)
				.collect(Collectors.toSet());
	}

	private float getWordsToMatchRatioWhenMissingDuration() {
		return minimumWordToMatchWhenMissingDurationPart1 / minimumWordToMatchWhenMissingDurationPart2;
	}
}
