package ro.go.adrhc.deduplicator.datasource.index.changes;

import com.rainerhahnekamp.sneakythrow.functional.SneakySupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class DefaultActualData<T> implements ActualData<T> {
	private final Function<String, T> documentIdToDataConverter;
	private final List<T> data;

	public static DefaultActualData<Path> actualPaths(
			SneakySupplier<List<Path>, IOException> dataSupplier) throws IOException {
		return new DefaultActualData<>(Path::of, new ArrayList<>(dataSupplier.get()));
	}

	@Override
	public boolean remove(String documentId) {
		T t = documentIdToDataConverter.apply(documentId);
		return data.remove(t);
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
