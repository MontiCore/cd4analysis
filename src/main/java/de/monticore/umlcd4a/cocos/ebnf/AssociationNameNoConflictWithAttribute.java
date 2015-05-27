package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that association names do not conflict with attributes in source
 * types.
 *
 * @author Robert Heim
 */
public class AssociationNameNoConflictWithAttribute implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A25";
  
  public static final String ERROR_MSG_FORMAT = "Association %s conflicts with the attribute %s in %s.";
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.getName().isPresent()) {
      String assocName = a.getName().get();
      Optional<CDTypeSymbol> leftType = a.getEnclosingScope().get()
          .resolve(a.getLeftReferenceName().toString(), CDTypeSymbol.KIND);
      Optional<CDTypeSymbol> rightType = a.getEnclosingScope().get()
          .resolve(a.getRightReferenceName().toString(), CDTypeSymbol.KIND);
      boolean err = false;
      // source type might be external (in this case we do nothing)
      if (leftType.isPresent() && (a.isLeftToRight() || a.isBidirectional() || a.isUnspecified())) {
        err = check(leftType.get(), assocName, a);
      }
      if (rightType.isPresent() && !err
          && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
        check(rightType.get(), assocName, a);
      }
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param sourceType source of the assoc under test
   * @param assocName the associations name
   * @param assoc association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, String assocName, ASTCDAssociation assoc) {
    // attributes
    Optional<CDFieldSymbol> conflictingAttribute = sourceType.getAllVisibleFields().stream()
        .filter(f -> f.getName().equals(assocName))
        .findAny();
    
    if (conflictingAttribute.isPresent()) {
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, assocName, assocName, conflictingAttribute.get()
              .getEnclosingScope().getSpanningSymbol().get().getName()),
          assoc.get_SourcePositionStart());
      return true;
    }
    
    // automatically introduced attributes from other assocs of the source type
    // that are not defined by assoc name (same assoc name would be found by
    // other coco)
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getAssociations().stream()
        .filter(a -> !a.getAssocName().isPresent())
        .filter(a -> a.getDerivedName().equals(assocName))
        .findAny();
    if (!conflictingAssoc.isPresent()) {
      // automatically introduced attributes from inherited associations
      conflictingAssoc = sourceType
          .getInheritedAssociations().stream()
          .filter(a -> a.getDerivedName().equals(assocName))
          .findAny();
    }
    if (conflictingAssoc.isPresent()) {
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT,
              assocName,
              assocName,
              conflictingAssoc.get().getSourceType().getName()),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }
}
