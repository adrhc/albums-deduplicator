# maven commands

mvn archetype:generate -DgroupId=ro.go.adrhc -DartifactId=deduplicator -DarchetypeArtifactId=maven-archetype-quickstart

# ERROR No command found for '--spring.config.additional-location=...

see NonInteractiveShellRunnerCustomizer

# run a particular test

```./mvnw test -Dtest="FilesIndexCreateServiceTest#findDuplicates"```