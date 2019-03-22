/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.BuiltInTypes;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 *
 * @author Robert Heim
 */
public class AssociationQualifierTypeExists
    implements CD4AnalysisASTCDAssociationCoCo {
  /**
   * @see de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    if (node.isPresentLeftQualifier()) {
      err = check(node.getLeftQualifier(), node);
    }
    
    if (!err && node.isPresentRightQualifier()) {
      check(node.getRightQualifier(), node);
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param qualifier qualifier under test
   * @param node the association under test
   * @return whether there was a coco error or not
   */
  private boolean check(ASTCDQualifier qualifier, ASTCDAssociation node) {
    boolean hasError = false;
    if (qualifier.isPresentType()) {
      ASTType type = qualifier.getType();
      String typeName = TypesPrinter.printType(type);
      if (!BuiltInTypes.isBuiltInType(typeName)) {
        Optional<CDTypeSymbol> typeSym = qualifier.getEnclosingScope()
            .resolve(typeName, CDTypeSymbol.KIND);
        if (!typeSym.isPresent()) {
          hasError = true;
          Log.error(
              String
                  .format(
                      "0xC4A19 The type %s of the typed qualified association %s could not be found. Only external datatypes and types defined within the classdiagram may be used.",
                      typeName,
                      CD4ACoCoHelper.printAssociation(node)),
              qualifier.get_SourcePositionStart());
        }
      }
    }
    return hasError;
  }
  
}
