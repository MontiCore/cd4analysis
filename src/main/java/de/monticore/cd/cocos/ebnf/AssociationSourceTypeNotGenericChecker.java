/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class AssociationSourceTypeNotGenericChecker implements
    CD4AnalysisASTCDDefinitionCoCo {
  
  public void check(ASTCDDefinition cdDefinition) {
    
    List<ASTCDAssociation> assocList = cdDefinition.getCDAssociationList();
    for (ASTCDAssociation assoc : assocList) {
      
      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
        
        printErrorOnGeneric(leftType, assoc);
      }
      
      if (assoc.isRightToLeft()) {
        ASTMCQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnGeneric(rightType, assoc);
      }
    }
  }
  
  private void printErrorOnGeneric(ASTMCQualifiedName sourceType,
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
