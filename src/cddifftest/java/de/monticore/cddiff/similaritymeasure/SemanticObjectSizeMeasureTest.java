/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.similaritymeasure;

import de.monticore.similaritymeasure.measures.SemanticObjectSizeMeasure;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test classes to test the similarity measure for semantic size differencing
 *
 */
public class SemanticObjectSizeMeasureTest extends AbstractTest {

  @Test
  public void testManger() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v2.cd");
    assertNotNull(astV2);

    SemanticObjectSizeMeasure measure = new SemanticObjectSizeMeasure();
    assertEquals(98.0, measure.difference(astV1, astV2), 0.0);
    assertEquals(measure.difference(astV1, astV2), measure.difference(astV2, astV1), 0.0);
  }
}
