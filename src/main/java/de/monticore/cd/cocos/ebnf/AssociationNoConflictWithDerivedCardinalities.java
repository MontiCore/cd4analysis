/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that derived associations have the same cardinality
 *
 * @author Michael von Wenckstern
 */
public class AssociationNoConflictWithDerivedCardinalities implements
    CD4AnalysisASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (!a.isDerived())
      return; // if it is not derived, than a role name conflict is detected by 0xCD4A33
    Optional<CDTypeSymbol> leftType = a.getEnclosingScope()
            .resolveCDType(a.getLeftReferenceName().toString());
    Optional<CDTypeSymbol> rightType = a.getEnclosingScope()
            .resolveCDType(a.getRightReferenceName().toString());
    boolean err = false;
    // source type might be external (in this case we do nothing)
    if (leftType.isPresent() && (a.isLeftToRight() || a.isBidirectional() || a.isUnspecified())) {
      err = check(leftType.get(), a.getRightRoleOpt(), a);
    }
    if (rightType.isPresent() && !err
            && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
      check(rightType.get(), a.getLeftRoleOpt(), a);
    }
  }

  /**
   * Does the actual check.
   *
   * @param sourceType source of the assoc under test
   * @param role       optional role name of the target type
   * @param assoc      association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, Optional<String> role, ASTCDAssociation assoc) {
    CDAssociationSymbol assocSym = (CDAssociationSymbol) assoc.getSymbol();

    String roleName = assocSym.getDerivedName();

    // inherited
    // do not check read-only, as it is checked by AssociationNoConflictWithCardinalities
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getInheritedAssociations().stream()
            .filter(a -> !a.isReadOnly() && a.getDerivedName().equals(roleName))
            .filter(a -> a != assocSym)
            .filter(a -> a.getTargetCardinality().getMin() != assocSym.getTargetCardinality().getMin()
              || a.getTargetCardinality().getMin() != assocSym.getTargetCardinality().getMin())
            .findAny();

    if (conflictingAssoc.isPresent()) {
      Log.error(
              String
                      .format(
                              "0xC4A37 The target cardinality (%s .. %s) of the derived (inherited) association `%s` does not math the target cardinality (%s .. %s) of the association `%s`",
                              assocSym.getTargetCardinality().getMin(),
                              String.valueOf(assocSym.getTargetCardinality().getMax()).replace("-1", "*"),
                              assocSym.isPresentAstNode() ? assocSym.getAstNode() : assocSym,
                              conflictingAssoc.get().getTargetCardinality().getMin(),
                              String.valueOf(conflictingAssoc.get().getTargetCardinality().getMax()).replace("-1", "*"),
                              conflictingAssoc.get().isPresentAstNode() ? conflictingAssoc.get().getAstNode() : conflictingAssoc.get()
                              ),
              assoc.get_SourcePositionStart());
      return true;
    }
    return false;

  }
}
