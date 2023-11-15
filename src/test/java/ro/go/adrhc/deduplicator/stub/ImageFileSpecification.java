package ro.go.adrhc.deduplicator.stub;

public record ImageFileSpecification(String filename, int size) {
	public static ImageFileSpecification of1024(String filename) {
		return new ImageFileSpecification(filename, 1024);
	}

	public static ImageFileSpecification of512(String filename) {
		return new ImageFileSpecification(filename, 512);
	}
}
