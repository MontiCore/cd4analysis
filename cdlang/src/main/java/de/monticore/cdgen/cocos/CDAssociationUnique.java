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
    for (ASTCDAssociation assoc2 : node.getCDAssociationsList()) {
      
      for (ASTCDAssociation assoc1 : alreadyChecked) {
        for (AssociationReference ref1 : getAssociationReferences(assoc1)) {
          for (AssociationReference ref2 : getAssociationReferences(assoc2)) {
            if (ref1.name.equals(ref2.name)) {
              checkRef(node, ref1.sourceType, ref2.sourceType, assoc2);
            }
          }
        }
      }
      
      alreadyChecked.add(assoc2);
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
    if (type1 == null || type2 == null) {
      return;
    }
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
    if (assoc.isPresentName()) {
      return StringUtils.uncapitalize(assoc.getName());
    }
    else if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    else {
      return StringUtils.uncapitalize(assocSide.getMCQualifiedType().getMCQualifiedName()
          .getBaseName());
    }
  }
  
  protected List<AssociationReference> getAssociationReferences(ASTCDAssociation assoc) {
    List<AssociationReference> references = new ArrayList<>();
    
    boolean navigableLeft = assoc.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean navigableRight = assoc.getCDAssocDir().isDefinitiveNavigableRight();
    boolean undirected = !navigableLeft && !navigableRight;
    
    if (navigableRight || undirected) {
      references.add(new AssociationReference(findTypeByFullName(assoc, assoc.getLeftQualifiedName()
          .getQName()), deriveRoleName(assoc, AssocSide.RIGHT)));
    }
    if (navigableLeft || undirected) {
      references.add(new AssociationReference(findTypeByFullName(assoc, assoc
          .getRightQualifiedName().getQName()), deriveRoleName(assoc, AssocSide.LEFT)));
    }
    
    return references;
  }
  
  private enum AssocSide {
    LEFT, RIGHT;
  }
  
  protected static class AssociationReference {
    
    protected final ASTCDType sourceType;
    
    protected final String name;
    
    protected AssociationReference(ASTCDType sourceType, String name) {
      this.sourceType = sourceType;
      this.name = name;
    }
    
  }
  
}
