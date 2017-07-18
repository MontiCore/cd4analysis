/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that attribute of the attribute-qualifier of an association exists in
 * the target class.
 *
 * @author Robert Heim
 */
public class AssociationQualifierAttributeExistsInTarget
    implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    if (node.getLeftQualifier().isPresent()) {
      err = check(node.getLeftQualifier().get(), node.getRightReferenceName(), node);
    }
    
    if (!err && node.getRightQualifier().isPresent()) {
      check(node.getRightQualifier().get(), node.getLeftReferenceName(), node);
    }
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
  private boolean check(ASTCDQualifier qualifier, ASTQualifiedName referencedType, ASTCDAssociation node) {
    boolean hasError = false;
    if (qualifier.getName().isPresent()) {
      String expectedAttributeName = qualifier.getName().get();
      Optional<CDTypeSymbol> referencedTypeSymOpt = node.getEnclosingScope().get()
          .resolve(referencedType.toString(), CDTypeSymbol.KIND);
      if (!referencedTypeSymOpt.isPresent()) {
        Log.error(String.format("0xC4A80 The referenced type %s cannot be resolved.",
            referencedType.toString()));
      }
      else {
        CDTypeSymbol referencedTypeSym = referencedTypeSymOpt.get();
        if(!referencedTypeSym.getAllVisibleFields().stream().map( x -> x.getName()).collect(Collectors.toList()).contains(expectedAttributeName)){
          hasError = true;
          Log.error(String.format("0xC4A20 The qualified association %s expects the attribute %s to exist in the referenced type %s.",
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
