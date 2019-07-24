/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.BuiltInTypes;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.symboltable.CDTypeSymbol;
import de.monticore.types.BasicGenericsTypesPrinter;
import de.monticore.types.BasicTypesPrinter;
import de.monticore.types.TypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.cd.cd4analysis._ast.ASTCDQualifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 *
 * @author Robert Heim
 */
public class AssociationQualifierTypeExists
    implements CD4AnalysisASTCDAssociationCoCo {
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.cd._ast.ASTCDAssociation)
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
    if (qualifier.isPresentMCType()) {
      ASTMCType type = qualifier.getMCType();
      String typeName = BasicGenericsTypesPrinter.printType(type);
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