/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CDAssociationHelperTest extends CDDiffTestBasis {
  protected final ASTCDCompilationUnit conflictCD =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/Conflict/ConflictEmployees.cd");

  @Test
  public void testInConflict() {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(conflictCD);
    List<ASTCDAssociation> assocList =
        new ArrayList<>(conflictCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation src : conflictCD.getCDDefinition().getCDAssociationsList()) {
      assocList.remove(src);
      Assert.assertTrue(
          assocList.stream()
              .anyMatch(target -> CDAssociationHelper.inConflict(src, target, scope)));
      Assert.assertFalse(
          assocList.stream()
              .allMatch(target -> CDAssociationHelper.inConflict(src, target, scope)));
      assocList.add(src);
    }
  }

  @Test
  public void testSameAssociation() {
    List<ASTCDAssociation> assocList =
        new ArrayList<>(conflictCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation src : conflictCD.getCDDefinition().getCDAssociationsList()) {
      assocList.remove(src);
      Assert.assertTrue(
          assocList.stream().noneMatch(target -> CDAssociationHelper.sameAssociation(src, target)));
      assocList.add(src);
    }
  }

  @Test
  public void testSuperAssociation() {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(conflictCD);
    List<ASTCDAssociation> assocList =
        new ArrayList<>(conflictCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation src : conflictCD.getCDDefinition().getCDAssociationsList()) {
      assocList.remove(src);
      Assert.assertTrue(
          assocList.stream()
              .anyMatch(
                  target ->
                      CDAssociationHelper.isSuperAssociation(src, target, scope)
                          || CDAssociationHelper.isSuperAssociationInReverse(src, target, scope)
                          || CDAssociationHelper.isSuperAssociation(target, src, scope)
                          || CDAssociationHelper.isSuperAssociationInReverse(target, src, scope)));
      Assert.assertFalse(
          assocList.stream()
              .allMatch(
                  target ->
                      CDAssociationHelper.isSuperAssociation(src, target, scope)
                          || CDAssociationHelper.isSuperAssociationInReverse(src, target, scope)
                          || CDAssociationHelper.isSuperAssociation(target, src, scope)
                          || CDAssociationHelper.isSuperAssociationInReverse(target, src, scope)));
      assocList.add(src);
    }
  }
}
