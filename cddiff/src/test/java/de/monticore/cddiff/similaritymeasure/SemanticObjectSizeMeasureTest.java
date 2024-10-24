/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.similaritymeasure;

import static org.junit.Assert.*;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Ignore;
import org.junit.Test;

/** Test classes to test the similarity measure for semantic size differencing */
public class SemanticObjectSizeMeasureTest extends CDDiffTestBasis {

  /**
   * Note: This test tests again a given "difference" and this difference depends on the specific
   * AST-CD , thus changes to upstream grammars cause this test to fail. In case this test is
   * desired again: Please test against concrete differences/etc. and not against an abstract
   * difference value that is subject to changes.
   */
  @Ignore
  @Test
  public void testManger() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 =
        parseModel("src/test/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 =
        parseModel("src/test/resources/de/monticore/cddiff/Employees/Employees2.cd");
    assertNotNull(astV2);

    SemanticObjectSizeMeasure measure = new SemanticObjectSizeMeasure();
    assertEquals(98.0, measure.difference(astV1, astV2), 0.0);
    assertEquals(measure.difference(astV1, astV2), measure.difference(astV2, astV1), 0.0);
  }
}
