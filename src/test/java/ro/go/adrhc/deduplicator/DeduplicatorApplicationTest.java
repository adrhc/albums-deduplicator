package ro.go.adrhc.deduplicator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.Shell;

/**
 * Unit test for simple App.
 */
@SpringBootTest
@ExcludeShellAutoConfiguration
@MockBean(classes = {Shell.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeduplicatorApplicationTest {
    @Test
    void contextLoads() {
    }
}
