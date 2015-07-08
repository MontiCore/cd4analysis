package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociationList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

public class AssociationSourceTypeNotGenericChecker implements
    CD4AnalysisASTCDDefinitionCoCo {
  
  public void check(ASTCDDefinition cdDefinition) {
    
    ASTCDAssociationList assocList = cdDefinition.getCDAssociations();
    for (ASTCDAssociation assoc : assocList) {
      
      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();
        
        printErrorOnGeneric(leftType, assoc);
      }
      
      if (assoc.isRightToLeft()) {
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnGeneric(rightType, assoc);
      }
    }
  }
  
  private void printErrorOnGeneric(ASTQualifiedName sourceType,
      ASTCDAssociation assoc) {
    
    String s = sourceType.toString();
    
    if (s.contains("<")) {
      
      String assocString = CD4ACoCoHelper.printAssociation(assoc);
      Log.error(
          String
              .format(
                  "0xD??? Association %s is invalid, because an association's source may not be a generic type",
                  assocString),
          assoc.get_SourcePositionStart());
      
    }
  }
}
