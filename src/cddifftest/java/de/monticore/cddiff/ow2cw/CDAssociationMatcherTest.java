package de.monticore.cddiff.ow2cw;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDAssociationMatcher;
import org.junit.Assert;
import org.junit.Test;


public class CDAssociationMatcherTest extends CDDiffTestBasis {

  @Test
  public void testSameAssociation() {
    ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/ow2cw/Lecture1.cd");
    ASTCDCompilationUnit lecture2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/ow2cw/Lecture2.cd");
    for (ASTCDAssociation assoc1 : lecture1.getCDDefinition().getCDAssociationsList()) {
      Assert.assertTrue(lecture2.getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .anyMatch(assoc2 -> CDAssociationMatcher.sameAssociation(assoc1, assoc2)));
    }
  }

}
