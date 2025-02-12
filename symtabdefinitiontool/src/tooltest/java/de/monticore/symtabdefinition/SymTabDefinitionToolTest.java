package de.monticore.symtabdefinition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymTabDefinitionToolTest extends AbstractToolTest {

  @Test
  public void testVersion() throws Exception {
    var pb = runToolProcess("-v");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    String version = System.getProperty("mc_version");

    assertContains(
      out, "SymTabDefinitionTool, version " + version + ", based on MontiCore version " + version);
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

    assertContains(out, "symbol table");
    assertContains(out, " -v,--version");
    assertContains(out, "-c2mc");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(0, process.exitValue(), "Exit code of: " + out);
  }

  @Test
  public void testCompleteAsInput() throws Exception {
    var pb = runToolProcess("-c2mc", "-c", "-i",
      "src/tooltest/resources/de/monticore/stdefinition/Complete.symtabdefinition"
    );
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    assertContains(out, "CoCo");
    assertContains(out, "passed");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(0, process.exitValue(), "Exit code of: " + out);
  }

  @Test
  public void testIncorrectOption() throws Exception {
    var pb = runToolProcess("-IAmCertainThatWeWillNotAddThisFlagInTheFuture");
    Process process = pb.start();
    String out = new String(process.getInputStream().readAllBytes());
    String err = new String(process.getErrorStream().readAllBytes());
    process.waitFor();

    assertContains(out, "0xCE0E3");
    Assertions.assertEquals("", err, "The error stream was not empty");
    assertNoStacktrace(out);
    assertNoStacktrace(err);
    Assertions.assertEquals(255, process.exitValue() & 0xFF, "Exit code of: " + out);
    // & 0xFF due to unsigned exit values (one some OSes)
  }

}
