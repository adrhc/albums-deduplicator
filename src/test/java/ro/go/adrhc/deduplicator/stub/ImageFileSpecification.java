package ro.go.adrhc.deduplicator.stub;

public record ImageFileSpecification(String filename, int size) {
	public static ImageFileSpecification of(String filename) {
		return new ImageFileSpecification(filename, 1024);
	}
}
