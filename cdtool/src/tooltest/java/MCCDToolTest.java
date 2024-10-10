import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that the shadowed tool jar conforms to a certain format
 *
 * <p>The property "toolJarFile" MUST contain the path to the CLI-jar The property "mc_version" MUST
 * contain the current version
 */
public class MCCDToolTest extends AbstractToolTest {

  @Test
  public void testVersion() throws Exception {
    var pb = runToolProcess("-v");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    String version = System.getProperty("mc_version");

    assertContains(
        out, "CD4CodeTool, version " + version + ", based on MontiCore version " + version);
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(0, process.exitValue(), "Exit code of: " + out);
  }

  @Test
  public void testHelp() throws Exception {
    var pb = runToolProcess("-h");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    assertContains(out, "usage: Examples in case the Tool file is called MCCD.jar");
    assertContains(out, " -v,--version                         Prints version information");
    assertContains(out, "Further details: https://www.se-rwth.de/topics/");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(0, process.exitValue(), "Exit code of: " + out);
  }

  @Test
  public void testCompleteAsInput() throws Exception {
    var pb = runToolProcess("-i", "src/test/resources/de/monticore/cd/Complete.cd");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    assertContains(out, "Successfully parsed src/test/resources/de/monticore/cd/Complete.cd");
    assertContains(out, "Successfully checked the CoCos for class diagram Complete");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(0, process.exitValue(), "Exit code of: " + out);
  }

  @Test
  public void testIncorrectOption() throws Exception {
    var pb = runToolProcess("-MissingOptionYeHawHawHaw");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    assertContains(out, "usage: Examples in case the Tool file is called MCCD.jar");
    assertContains(out, " -v,--version                         Prints version information");
    assertContains(out, "Further details: https://www.se-rwth.de/topics/");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(255, process.exitValue() & 0xFF, "Exit code of: " + out);
    // & 0xFF due to unsigned exit values (one some OSes)
  }
}
