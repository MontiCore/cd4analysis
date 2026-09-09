/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Checks that there are not multiple occurrences of the same association between types
 */
public class CDAssociationUnique implements CDBasisASTCDDefinitionCoCo {
  
  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDDefinition node) {
    
    List<ASTCDAssociation> alreadyChecked = new ArrayList<>();
    
    // we check for each pair of associations
    for (ASTCDAssociation assoc1 : node.getCDAssociationsList()) {
      
      alreadyChecked.add(assoc1);
      
      for (ASTCDAssociation assoc2 : node.getCDAssociationsList()) {
        
        // only check each pair once
        if (assoc2 != assoc1 && !alreadyChecked.contains(assoc2)) {
          
          // if they allow navigation from left to right and share a right role-name,
          // the referenced types on the left should not be the same
          if (assoc1.getCDAssocDir().isDefinitiveNavigableRight() && assoc2.getCDAssocDir()
              .isDefinitiveNavigableRight() && deriveRoleName(assoc1, AssocSide.RIGHT).equals(
                  deriveRoleName(assoc2, AssocSide.RIGHT))) {
            checkRef(node, findTypeByFullName(assoc1, assoc1.getLeftQualifiedName().getQName()),
                findTypeByFullName(assoc2, assoc2.getLeftQualifiedName().getQName()), assoc1);
          }
          
          // if they allow navigation from right to left and share a left role-name,
          // the referenced types on the right should not be the same
          if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
              .isDefinitiveNavigableLeft() && deriveRoleName(assoc1, AssocSide.LEFT).equals(
                  deriveRoleName(assoc2, AssocSide.LEFT))) {
            checkRef(node, findTypeByFullName(assoc1, assoc1.getRightQualifiedName().getQName()),
                findTypeByFullName(assoc2, assoc2.getRightQualifiedName().getQName()), assoc1);
          }
          
          // We also consider a left-to-right role name and navigation match ...
          if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
              .isDefinitiveNavigableRight() && deriveRoleName(assoc1, AssocSide.LEFT).equals(
                  deriveRoleName(assoc2, AssocSide.RIGHT))) {
            checkRef(node, findTypeByFullName(assoc1, assoc1.getRightQualifiedName().getQName()),
                findTypeByFullName(assoc2, assoc2.getLeftQualifiedName().getQName()), assoc1);
          }
          // ... as well as a right-to-left match
          if (assoc1.getCDAssocDir().isDefinitiveNavigableRight() && assoc2.getCDAssocDir()
              .isDefinitiveNavigableLeft() && deriveRoleName(assoc1, AssocSide.RIGHT).equals(
                  deriveRoleName(assoc2, AssocSide.LEFT))) {
            checkRef(node, findTypeByFullName(assoc1, assoc1.getLeftQualifiedName().getQName()),
                findTypeByFullName(assoc2, assoc2.getRightQualifiedName().getQName()), assoc1);
          }
        }
      }
    }
  }
  
  /**
   * helper-method to find types by full-name
   */
  protected ASTCDType findTypeByFullName(ASTCDAssociation node, String fullName) {
    
    Optional<CDTypeSymbol> optSymbol = node.getEnclosingScope().resolveCDType(fullName);
    if (optSymbol.isPresent()) {
      return optSymbol.get().getAstNode();
    }
    
    Log.error("0xCDCE2: Could not find: " + fullName + ".");
    return null;
  }
  
  /** Check if type2 is the same as type1. */
  protected void checkRef(ASTCDDefinition node, ASTCDType type1, ASTCDType type2,
      ASTCDAssociation assoc1) {
    if (type1.equals(type2)) {
      Log.error(String.format("0xCDCE1: %s has a duplicate association to %s", type1.getName(),
          type2.getName()), assoc1.isPresent_SourcePositionStart() ? assoc1
              .get_SourcePositionStart() : null, assoc1.isPresent_SourcePositionEnd() ? assoc1
                  .get_SourcePositionEnd() : null);
    }
  }
  
  /** derive role name if not present */
  protected String deriveRoleName(ASTCDAssociation assoc, AssocSide side) {
    ASTCDAssocSide assocSide;
    if (side.equals(AssocSide.LEFT)) {
      assocSide = assoc.getLeft();
    }
    else {
      assocSide = assoc.getRight();
    }
    if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    else if (assoc.isPresentName()) {
      return StringUtils.uncapitalize(assoc.getName());
    }
    else {
      return StringUtils.uncapitalize(assocSide.getMCQualifiedType().getMCQualifiedName()
          .getBaseName());
    }
  }
  
  private enum AssocSide {
    LEFT, RIGHT;
  }
  
}
