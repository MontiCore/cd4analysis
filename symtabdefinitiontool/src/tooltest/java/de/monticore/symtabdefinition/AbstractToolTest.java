package de.monticore.symtabdefinition;

import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Better Option: copied from cdtool
 * -> a common place to store helper classes for tests would be useful
 *
 * Test that the shadowed tool jar conforms to a certain format
 *
 * <p>The property "toolJarFile" MUST contain the path to the CLI-jar
 */
public abstract class AbstractToolTest {

  protected void assertNoStacktrace(String haystack) {
    final Pattern pattern = Pattern.compile("at [a-zA-z]+\\.[a-zA-z]+");
    if (pattern.matcher(haystack).find()) {
      Assertions.fail("It appears we found a stacktrace in: " + haystack);
    }
  }

  protected void assertContains(String haystack, String needle) {
    this.assertContains("%1$s did not contain `%2$s`", haystack, needle);
  }

  protected void assertNotContains(String haystack, String needle) {
    this.assertNotContains("%1$s did contain `%2$s`", haystack, needle);
  }

  protected void assertContains(String message, String haystack, String needle) {
    if (!haystack.contains(needle)) {
      Assertions.fail(String.format(message, haystack, needle));
    }
  }

  protected void assertNotContains(String message, String haystack, String needle) {
    if (haystack.contains(needle)) {
      Assertions.fail(String.format(message, haystack, needle));
    }
  }

  protected ProcessBuilder runToolProcess(String... args) {
    File jarFile = new File(System.getProperty("toolJarFile"));

    Assertions.assertTrue(jarFile.exists());

    Optional<String> cmdOpt = ProcessHandle.current().info().command();
    Assertions.assertTrue(cmdOpt.isPresent());

    List<String> cmd = new ArrayList<>();
    cmd.add(cmdOpt.get());
    cmd.add("-jar");
    cmd.add(jarFile.getAbsolutePath());
    cmd.addAll(Arrays.asList(args));

    return new ProcessBuilder(cmd);
  }
}
