package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks if the source of an association is defined within the classdiagram
 * itself.
 *
 * @author Robert Heim
 */
public class AssociationSourceTypeNotExternalCoCo implements
    CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A22";
  
  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be an external type.";
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isLeftToRight() || assoc.isBidirectional()) {
      ASTQualifiedName leftType = assoc.getLeftReferenceName();
      if (isExternal(leftType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isRightToLeft() || assoc.isBidirectional()) {
      ASTQualifiedName rightType = assoc.getRightReferenceName();
      if (isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isUnspecified()) {
      ASTQualifiedName leftType = assoc.getLeftReferenceName();
      ASTQualifiedName rightType = assoc.getRightReferenceName();
      // not both can be external, but one is ok
      if (isExternal(leftType, assoc) && isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
  }
  
  private void error(ASTCDAssociation assoc) {
    String assocString = "";
    if (assoc.getName().isPresent()) {
      assocString = assoc.getName().get();
    }
    else {
      assocString = CD4ACoCoHelper.printAssociation(assoc);
    }
    CoCoLog.error(ERROR_CODE,
        String.format(ERROR_MSG_FORMAT, assocString),
        assoc.get_SourcePositionStart());
  }
  
  private boolean isExternal(ASTQualifiedName sourceType, ASTCDAssociation assoc) {
    Optional<CDTypeSymbol> sourceSym = assoc.getEnclosingScope().get()
        .resolve(sourceType.toString(), CDTypeSymbol.KIND);
    System.err.println(sourceType.toString());
    if (sourceSym.isPresent()) {
      return false;
    }
    return true;
  }
  
}
