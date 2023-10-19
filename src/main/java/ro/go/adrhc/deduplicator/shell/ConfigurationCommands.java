package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ro.go.adrhc.deduplicator.config.contextpaths.ContextPaths;
import ro.go.adrhc.deduplicator.config.contextpaths.ContextPathsStringifier;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.springframework.shell.standard.ShellOption.NULL;

@ShellComponent("Configuration management.")
@RequiredArgsConstructor
@Slf4j
public class ConfigurationCommands {
	private final ApplicationContext ac;
	private final ContextPaths contextPaths;
	private final ContextPathsStringifier stringifier;

	@ShellMethod("Set (optionally) the paths of the used resources.")
	public void setPaths(@ShellOption(defaultValue = NULL) Path indexPath,
			@ShellOption(defaultValue = NULL) Path filesPath) {
		contextPaths.update(indexPath, filesPath);
		log.debug("\n{}", stringifier.toString(contextPaths));
	}

	@ShellMethod(value = "Show the known paths.")
	public void showPaths() {
		log.debug("\n{}", stringifier.toString(contextPaths));
	}

	@ShellMethod("Show the application startup configuration.")
	public void showConfig() {
		configurationProperties().forEach(p -> log.debug("\n{}", p));
	}

	private Stream<?> configurationProperties() {
		return ac.getBeansWithAnnotation(ConfigurationProperties.class)
				.values().stream()
				.filter(p -> p.getClass().getName().startsWith("ro.go.adrhc.deduplicator"));
	}
}
