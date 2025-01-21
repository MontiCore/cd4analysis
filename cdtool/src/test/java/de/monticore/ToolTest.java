/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertTrue;

import de.monticore.cd.OutTestBasis;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.ExpectedException;

public class ToolTest extends OutTestBasis {
  @SuppressWarnings("deprecation")
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  protected static final String TOOL_PATH = "src/test/resources/de/monticore/";

  protected void assertContains(String haystack, String needle) {
    if (!haystack.contains(needle)) {
      Assertions.fail(String.format("%1$s did not contain `%2$s`", haystack, needle));
    }
  }

  @Test
  public void testMerge() {
    final String cd1 = "src/test/resources/doc/MyEmployees2.cd";
    final String cd2 = "src/test/resources/doc/MyEmployees1.cd";
    CD4CodeTool.main(new String[]{"-i", cd1, "--merge", cd2});

    assertContains(getOut(), "Successfully parsed src/test/resources/doc/MyEmployees2.cd");
    assertContains(getOut(), "Successfully checked the CoCos for class diagram Merge");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testMultiMerge() {
    final String cd1 = TOOL_PATH + "cdmerge/Person/A.cd";
    final String cd2 = TOOL_PATH + "cdmerge/Person/B.cd";
    final String cd3 = TOOL_PATH + "cdmerge/Person/C.cd";
    final String out = "target/generated/multi-merge";

    CD4CodeTool.main(new String[]{"-i", cd1, "--merge", cd2, cd3, "-o", out, "-pp", "Merge.cd"});

    assertContains(getOut(), "Successfully parsed src/test/resources/de/monticore/cdmerge/Person/A.cd");
    assertContains(getOut(), "successful");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testTool() throws IOException, ParseException {
    final String cd = TOOL_PATH + "cd/Complete.cd";

    CD4CodeTool.main(new String[]{"-i", cd, "-f", "false"});

    assertContains(getOut(), "Successfully parsed src/test/resources/de/monticore/cd/Complete.cd");
    assertContains(getOut(), "Successfully checked the CoCos for class diagram Complete");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testHelp() throws IOException, ParseException {
    final String cd = TOOL_PATH + "cd/Complete.cd";

    CD4CodeTool.main(new String[]{"-i", cd, "-h", "-f", "false"});

    assertContains(getOut(), "usage:");
    assertContains(getOut(), "Further details: https://www.se-rwth.de/topics/");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testToolNoBuiltInTypes() throws IOException, ParseException {
    final String cd = TOOL_PATH + "cd/Complete.cd";

    CD4CodeTool.main(new String[]{"-i", cd, "-nt", "-f", "false"});

    assertContains(getOut(), "Successfully parsed src/test/resources/de/monticore/cd/Complete.cd");
    assertContains(getOut(), "Successfully checked the CoCos for class diagram Complete");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testSymbolPath() throws IOException, ParseException {
    final String cd = TOOL_PATH + "cd/Complete.cd";

    CD4CodeTool.main(new String[]{"-i", cd, "-f", "false"});

    assertContains(getOut(), "Successfully parsed src/test/resources/de/monticore/cd/Complete.cd");
    assertContains(getOut(), "Successfully checked the CoCos for class diagram Complete");
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
      new String[]{
        "-i", fileName, "-f", "false", "-pp", getTmpFilePath("Complete.puml"), "-puml"
      });

    assertTrue(modelFileExists(getTmpFilePath("Complete.puml")));
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML2() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
      new String[]{
        "-i",
        fileName,
        "-f",
        "--pp",
        getTmpFilePath("Complete.svg"),
        "--puml",
        "--svg",
        "--showAttr"
      });

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML3() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
      new String[]{"-i", fileName, "-f", "--puml", "--svg", "-attr", "assoc", "--showRoles"});

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }

  @Test
  @Ignore // TODO JRa: success depends on gradle.properties genTR
  public void testTrafoTemplate() {
    final File cd = new File(TOOL_PATH + "trafo/Vehicle1.cd");
    final File trafoFp = new File(TOOL_PATH + "trafo/");
    assertTrue(cd.exists());
    assertTrue(trafoFp.exists());

    CD4CodeTool.main(new String[]{"-i", cd.toString(), "-fp", trafoFp.toString(), "--trafoTemplate", "VehicleTrafo", "-pp"});

    assertContains(getOut(), "public void setYear(Integer year)");
    assertTrue(getErr(), getErr().isEmpty());
  }
}
