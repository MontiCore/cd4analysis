/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.BuiltInTypes;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that the types connected by associations exist.
 *
 * @author Robert Heim
 */
public class AssociationSrcAndTargetTypeExistChecker implements
    CD4AnalysisASTCDAssociationCoCo {
  
  public void check(ASTCDAssociation assoc) {
    
    CDTypeSymbol src = ((CDAssociationSymbol) assoc.getSymbol()).getSourceType();
    checkTypeExists(src, assoc);
    
    CDTypeSymbol target = ((CDAssociationSymbol) assoc.getSymbol()).getTargetType();
    checkTypeExists(target, assoc);
    
    // ASTQualifiedName leftType = assoc.getLeftReferenceName();
    // ASTQualifiedName rightType = assoc.getRightReferenceName();
    
  }
  
  private void checkTypeExists(CDTypeSymbol type, ASTCDAssociation assoc) {
    String typeName = type.getName();
    if (!BuiltInTypes.isBuiltInType(typeName)) {
      Optional<CDTypeSymbol> subClassSym = assoc.getEnclosingScope()
          .resolveCDType(typeName);
      if (!subClassSym.isPresent()) {
        String assocString = CD4ACoCoHelper.printAssociation(assoc);
        Log.error(
            String
                .format(
                    "0xC4A36 Type %s of association %s is unknown.", typeName, assocString),
            assoc.get_SourcePositionStart());
      }
    }
  }
}
