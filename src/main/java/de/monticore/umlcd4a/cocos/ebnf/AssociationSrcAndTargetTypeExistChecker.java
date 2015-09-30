/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.umlcd4a.BuiltInTypes;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the types connected by associations exist.
 *
 * @author Robert Heim
 */
public class AssociationSrcAndTargetTypeExistChecker implements
    CD4AnalysisASTCDAssociationCoCo {
  
  public void check(ASTCDAssociation assoc) {
    
    CDTypeSymbol src = ((CDAssociationSymbol) assoc.getSymbol().get()).getSourceType();
    checkTypeExists(src, assoc);
    
    CDTypeSymbol target = ((CDAssociationSymbol) assoc.getSymbol().get()).getTargetType();
    checkTypeExists(target, assoc);
    
    // ASTQualifiedName leftType = assoc.getLeftReferenceName();
    // ASTQualifiedName rightType = assoc.getRightReferenceName();
    
  }
  
  private void checkTypeExists(CDTypeSymbol type, ASTCDAssociation assoc) {
    String typeName = type.getName();
    if (!BuiltInTypes.isBuiltInType(typeName)) {
      Optional<CDTypeSymbol> subClassSym = assoc.getEnclosingScope().get()
          .resolve(typeName, CDTypeSymbol.KIND);
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
