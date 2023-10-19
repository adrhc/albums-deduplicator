package ro.go.adrhc.deduplicator.datasource.index.changes;

import com.rainerhahnekamp.sneakythrow.functional.SneakySupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class DefaultActualData<T> implements ActualData<T> {
	private final Function<String, Optional<T>> documentIdToDataConverter;
	private final List<T> data;

	public static DefaultActualData<Path> actualPaths(
			SneakySupplier<List<Path>, IOException> dataSupplier) throws IOException {
		return new DefaultActualData<>(id -> Optional.of(Path.of(id)), dataSupplier.get());
	}

	@Override
	public boolean remove(String documentId) {
		Optional<T> optionalT = documentIdToDataConverter.apply(documentId);
		if (optionalT.isEmpty()) {
			log.error("The indexed object identified by {} can't " +
					"be converted to actual-data type!", documentId);
			return false;
		} else {
			return data.remove(optionalT.get());
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Collection<T> toCollection() {
		return data;
	}
}
