package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDAssociationHelper;
import org.junit.Assert;
import org.junit.Test;

public class CDAssociationHelperTest extends CDDiffTestBasis {
  protected final ASTCDCompilationUnit conflictCD = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Conflict/ConflictEmployees.cd");
  @Test
  public void testInConflict(){
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(conflictCD);
    for (ASTCDAssociation src : conflictCD.getCDDefinition().getCDAssociationsList()){
      Assert.assertTrue(conflictCD.getCDDefinition().getCDAssociationsList().stream().anyMatch(target -> CDAssociationHelper.inConflict(src,target,scope)));
    }
  }

}
