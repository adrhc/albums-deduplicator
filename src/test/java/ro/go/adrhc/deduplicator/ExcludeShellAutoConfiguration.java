package ro.go.adrhc.deduplicator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.shell.boot.JLineShellAutoConfiguration;
import org.springframework.shell.boot.LineReaderAutoConfiguration;
import org.springframework.shell.boot.SpringShellAutoConfiguration;

@EnableAutoConfiguration(exclude = {
        SpringShellAutoConfiguration.class,
        JLineShellAutoConfiguration.class,
        LineReaderAutoConfiguration.class})
public @interface ExcludeShellAutoConfiguration {
}
