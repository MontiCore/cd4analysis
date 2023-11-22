package de.monticore.odvalidity;

import static org.junit.Assert.fail;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;

public class ExampleDiffTest extends CDDiffTestBasis {
  @Test
  public void testMyDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/MyDiffs/CD12.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/MyDiffs/CD11.cd";

    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());

    try {
      for (int i = 1; i <= 5; i++) {
        Assert.assertTrue(
            new OD2CDMatcher()
                .checkIfDiffWitness(
                    CDSemantics.SIMPLE_CLOSED_WORLD,
                    Path.of(cd1).toFile(),
                    Path.of(cd2).toFile(),
                    Path.of("src/cddifftest/resources/de/monticore/cddiff/MyDiffs/D" + i + ".od")
                        .toFile()));
      }
    } catch (NullPointerException | IOException e) {
      fail(e.getMessage());
    }
  }
}
