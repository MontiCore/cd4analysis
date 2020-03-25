/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDQualifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that attribute of the attribute-qualifier of an association exists in
 * the target class.
 *
 */
public class AssociationQualifierAttributeExistsInTarget
    implements CD4AnalysisASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean err = false;
    
    if (node.isPresentLeftQualifier()) {
      err = check(node.getLeftQualifier(), node.getRightReferenceName(), node);
    }
    
    if (!err && node.isPresentRightQualifier()) {
      check(node.getRightQualifier(), node.getLeftReferenceName(), node);
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
  private boolean check(ASTCDQualifier qualifier, ASTMCQualifiedName referencedType, ASTCDAssociation node) {
    boolean hasError = false;
    if (qualifier.isPresentName()) {
      String expectedAttributeName = qualifier.getName();
      Optional<CDTypeSymbol> referencedTypeSymOpt = node.getEnclosingScope()
          .resolveCDType(referencedType.toString());
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
