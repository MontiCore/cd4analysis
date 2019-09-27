/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 *
 * @author Robert Heim
 */
public class AssociationSourceNotEnum
    implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.cd._ast.ASTCDAssociation)
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
  private boolean check(ASTMCQualifiedName sourceName, ASTCDAssociation node) {
    boolean hasError = false;
    Optional<CDTypeSymbol> sourceSym = node.getEnclosingScope()
        .resolveCDType(sourceName.toString());
    if (sourceSym.isPresent() && sourceSym.get().isIsEnum()) {
      hasError = true;
      Log.error(
          String
              .format(
                  "0xC4A21 Association %s is invalid, because an association's source may not be an Enumeration.",
                  CD4ACoCoHelper.printAssociation(node)),
          node.get_SourcePositionStart());
    }
    return hasError;
  }
  
}
