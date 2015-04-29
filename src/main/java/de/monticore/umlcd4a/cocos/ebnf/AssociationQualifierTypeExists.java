/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.BuiltInTypes;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDQualifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 *
 * @author Robert Heim
 */
public class AssociationQualifierTypeExists
    implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A19";
  
  public static final String ERROR_MSG_FORMAT = "The type %s of the typed qualified association %s could not be found. Only external datatypes and types defined within the classdiagram may be used.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    if (node.getLeftQualifier().isPresent()) {
      err = check(node.getLeftQualifier().get(), node);
    }
    
    if (!err && node.getRightQualifier().isPresent()) {
      check(node.getRightQualifier().get(), node);
    }
  }
  
  /**
   * TODO derived attribute in ast?
   * 
   * @param qualifier
   * @return
   */
  private boolean isTypeQualifier(ASTCDQualifier qualifier) {
    // TODO must always be name and not type see #1626
    return Character.isUpperCase(qualifier.getName().get().charAt(0));
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
    if (isTypeQualifier(qualifier)) {
      // TODO must always be name and not type see #1626
      String typeName = qualifier.getName().get();
      if (!BuiltInTypes.isBuiltInType(typeName)) {
        Optional<CDTypeSymbol> subClassSym = qualifier.getEnclosingScope().get()
            .resolve(typeName, CDTypeSymbol.KIND);
        if (!subClassSym.isPresent()) {
          hasError = true;
          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT,
                  typeName,
                  CD4ACoCoHelper.printAssociation(node)),
              qualifier.get_SourcePositionStart());
        }
      }
    }
    return hasError;
  }
  
}
