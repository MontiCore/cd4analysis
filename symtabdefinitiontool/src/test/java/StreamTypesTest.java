import de.monticore.symtabdefinition.SymTabDefinitionTool;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamTypesTest {

  @BeforeEach
  public void init() {
    LogStub.init();
  }

  @Test
  public void testExportST() {
    final String modelPath = "src/test/resources/streamTypes/";
    File targetDir = Files.createTempDir("tmpDirTestExportST");
    assertTrue(targetDir.exists());
    SymTabDefinitionTool.main(
      new String[] {
        "-c2mc",
        "-path",
        modelPath,
        "-s",
        targetDir.getAbsolutePath(),
        "-i",
        "src/test/resources/streamTypes/Stream.symtabdefinition",
        "src/test/resources/streamTypes/EventStream.symtabdefinition",
        "src/test/resources/streamTypes/SyncStream.symtabdefinition",
        "src/test/resources/streamTypes/ToptStream.symtabdefinition",
        "src/test/resources/streamTypes/UntimedStream.symtabdefinition"
      });
    assertTrue(Log.getFindings().isEmpty());
    assertEquals(5, targetDir.listFiles().length);
    targetDir.delete();
  }
}
