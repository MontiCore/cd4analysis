/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that attribute of the attribute-qualifier of an association exists in
 * the target class.
 *
 * @author Robert Heim
 */
public class AssociationQualifierAttributeExistsInTarget
    implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A20";
  
  public static final String ERROR_MSG_FORMAT = "The qualified association %s expects the attribute %s to exist in the referenced type %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    // TODO RH checks einkommentieren wenn #1627 bearbeitet wurde
    if (node.getLeftQualifier().isPresent()) {
      // err = check(node.getLeftQualifier().get(),
      // node.getRightReferenceName(), node);
    }
    
    if (!err && node.getRightQualifier().isPresent()) {
      // check(node.getRightQualifier().get(), node.getLeftReferenceName(),
      // node);
    }
  }
  
  /**
   * TODO derived attribute in ast?
   * 
   * @param qualifier
   * @return
   */
  private boolean isAttributeQualifier(ASTCDQualifier qualifier) {
    // TODO must always be name and not type see #1626
    return Character.isLowerCase(qualifier.getName().get().charAt(0));
  }
  
  /**
   * Does the actual check.
   * 
   * @param qualifier qualifier under test * @param referencedClass the
   * referenced class. Note that it must be a class because attributes may only
   * exist within classes.
   * @param node the association under test
   * @return whether there was a coco error or not
   */
  private boolean check(ASTCDQualifier qualifier, ASTQualifiedName referencedType,
      ASTCDAssociation node) {
    boolean hasError = false;
    if (isAttributeQualifier(qualifier)) {
      // TODO must always be name and not type see #1626
      String expectedAttributeName = qualifier.getName().get();
      Optional<CDTypeSymbol> referencedTypeSymOpt = node.getEnclosingScope().get()
          .resolve(referencedType.toString(), CDTypeSymbol.KIND);
      if (!referencedTypeSymOpt.isPresent()) {
        // TODO symbol must exist??? s. #1627
      }
      else {
        CDTypeSymbol referencedTypeSym = referencedTypeSymOpt.get();
        if (!referencedTypeSym.getField(expectedAttributeName).isPresent()) {
          hasError = true;
          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT,
                  CD4ACoCoHelper.printAssociation(node),
                  expectedAttributeName,
                  referencedTypeSym.getName()),
              qualifier.get_SourcePositionStart());
        }
      }
    }
    return hasError;
  }
  
}
