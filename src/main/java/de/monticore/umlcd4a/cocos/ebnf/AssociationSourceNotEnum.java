/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 *
 * @author Robert Heim
 */
public class AssociationSourceNotEnum
    implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A21";
  
  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because an association's source may not be an Enumeration.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    if (node.isLeftToRight() || node.isBidirectional() || node.isUnspecified()) {
      err = check(node.getLeftReferenceName(), node);
    }
    
    if (!err && (node.isRightToLeft() || node.isBidirectional() || node.isUnspecified())) {
      check(node.getRightReferenceName(), node);
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param sourceName the referenced name that may not be an enum
   * @param node the association under test
   * @return whether there was a coco error or not
   */
  private boolean check(ASTQualifiedName sourceName, ASTCDAssociation node) {
    boolean hasError = false;
    Optional<CDTypeSymbol> sourceSym = node.getEnclosingScope().get()
        .resolve(sourceName.toString(), CDTypeSymbol.KIND);
    if (sourceSym.isPresent() && sourceSym.get().isEnum()) {
      hasError = true;
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT,
              CD4ACoCoHelper.printAssociation(node)),
          node.get_SourcePositionStart());
    }
    return hasError;
  }
  
}
