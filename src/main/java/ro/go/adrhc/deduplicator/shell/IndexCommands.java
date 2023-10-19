package ro.go.adrhc.deduplicator.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import ro.go.adrhc.deduplicator.config.contextpaths.ContextPaths;
import ro.go.adrhc.deduplicator.config.contextpaths.ContextPathsStringifier;

@ShellComponent("Files index management.")
@RequiredArgsConstructor
@Slf4j
public class IndexCommands {
	private final ContextPaths contextPaths;
	private final ContextPathsStringifier stringifier;
}
