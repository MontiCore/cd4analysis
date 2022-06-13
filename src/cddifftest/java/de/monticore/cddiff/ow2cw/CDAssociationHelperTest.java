package de.monticore.cddiff.ow2cw;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDAssociationHelper;
import org.junit.Assert;
import org.junit.Test;

public class CDAssociationHelperTest extends CDDiffTestBasis {
  protected final ASTCDCompilationUnit lecture1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

  protected final ASTCDCompilationUnit lecture2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture2.cd");

  @Test
  public void testSameAssociation() {
    for (ASTCDAssociation assoc1 : lecture1.getCDDefinition().getCDAssociationsList()) {
      Assert.assertTrue(lecture2.getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .anyMatch(assoc2 -> CDAssociationHelper.sameAssociation(assoc1, assoc2)));
    }
  }

}
