/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that association roles does not conflict with declared Attributes in referred types
 */
// TODO SVa: still relevant? CDRoleSymbol !extends FieldSymbol
public class CDAssociationRoleNameNoConflictWithLocalAttribute
  implements CDBasisASTCDDefinitionCoCo {

  @Override
  public void check(ASTCDDefinition a) {
    a.getCDAssociationsList().stream()
      .filter(e -> e.getRight().isPresentCDRole() || e.getLeft().isPresentCDRole())
      .forEach(this::checkRoleNoConflict);
  }

  private void checkRoleNoConflict(ASTCDAssociation association) {
    if (association.getLeft().isPresentCDRole()) {
      // The role may only conflict with an attribute of the opposite Type
      checkRoleNoConflict(association.getLeft(), association.getRight());
    }
    if (association.getRight().isPresentCDRole()) {
      // The role may only conflict with an attribute of the opposite Type
      checkRoleNoConflict(association.getRight(), association.getLeft());
    }
  }

  private void checkRoleNoConflict(ASTCDAssocSide roleSide, ASTCDAssocSide referenceSide) {
    Optional<CDTypeSymbol> type = referenceSide.getEnclosingScope()
      .resolveCDType(referenceSide.getMCQualifiedType().getMCQualifiedName().getQName());

    if (type.isPresent() &&
      type.get().getSpannedScope().getFieldSymbols().containsKey(roleSide.getCDRole().getName())) {
      Log.error(
        String.format(
          "0xC4A27: Association role (%1$s) %2$s conflicts with attribute %1$s in reference Type %3$s.",
          roleSide.getCDRole().getName(),
          roleSide.getSymbol().getType().getTypeInfo().getName(),
          referenceSide.getSymbol().getType().getTypeInfo().getName()),
        roleSide.get_SourcePositionStart());
    }
  }
}
