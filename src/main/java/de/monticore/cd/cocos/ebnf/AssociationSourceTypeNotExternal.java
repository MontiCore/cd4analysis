/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks if the source of an association is defined within the classdiagram
 * itself.
 *
 */
public class AssociationSourceTypeNotExternal implements
    CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isLeftToRight() || assoc.isBidirectional()) {
      ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
      if (isExternal(leftType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isRightToLeft() || assoc.isBidirectional()) {
      ASTMCQualifiedName rightType = assoc.getRightReferenceName();
      if (isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isUnspecified()) {
      ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
      ASTMCQualifiedName rightType = assoc.getRightReferenceName();
      // not both can be external, but one is ok
      if (isExternal(leftType, assoc) && isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
  }
  
  private void error(ASTCDAssociation assoc) {
    String assocString = "";
    if (assoc.isPresentName()) {
      assocString = assoc.getName();
    }
    else {
      assocString = CD4ACoCoHelper.printAssociation(assoc);
    }
    Log.error(
        String
            .format(
                "0xC4A22 Association %s is invalid, because an association's source may not be an external type.",
                assocString),
        assoc.get_SourcePositionStart());
  }
  
  private boolean isExternal(ASTMCQualifiedName sourceType, ASTCDAssociation assoc) {
    Optional<CDTypeSymbol> sourceSym = assoc.getEnclosingScope()
        .resolveCDType(sourceType.toString());
    if (sourceSym.isPresent()) {
      return false;
    }
    return true;
  }
  
}
