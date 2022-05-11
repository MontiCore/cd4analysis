package de.monticore.cddiff.ow2cw;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDAssociationMatcher;
import org.junit.Assert;
import org.junit.Test;

public class CDAssociationMatcherTest extends CDDiffTestBasis {
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
          .anyMatch(assoc2 -> CDAssociationMatcher.sameAssociation(assoc1, assoc2)));
    }
  }

  @Test
  public void testUpdateDir2Match() {
    CDAssociationMatcher.updateDir2Match(lecture1.getCDDefinition().getCDAssociationsList(),
        lecture2.getCDDefinition().getCDAssociationsList());

    Assert.assertTrue(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .anyMatch(assoc2 -> assoc2.getCDAssocDir().isBidirectional()));

    Assert.assertTrue(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .allMatch(assoc2 -> assoc2.getCDAssocDir().isDefinitiveNavigableRight()));

    Assert.assertFalse(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .allMatch(
            assoc2 -> assoc2.getCDAssocDir().isBidirectional()));
  }

}
