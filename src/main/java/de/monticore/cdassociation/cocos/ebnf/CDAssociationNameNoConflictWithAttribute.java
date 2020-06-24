/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that association names do not conflict with attributes in source
 * types.
 */
public class CDAssociationNameNoConflictWithAttribute
    implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (!a.isPresentSymAssociation()) {
      return;
    }

    final CDRoleSymbol left = a.getSymAssociation().getLeft();
    final CDRoleSymbol right = a.getSymAssociation().getRight();

    final CDAssociationNavigableVisitor navigableVisitor = new CDAssociationNavigableVisitor();
    a.accept(navigableVisitor);

    if (navigableVisitor.isDefinitiveNavigableRight()) {
      check(left.getType().getTypeInfo(), right.getType().getTypeInfo(), left.getName(), a);
    }
    if (navigableVisitor.isDefinitiveNavigableLeft()) {
      check(right.getType().getTypeInfo(), left.getType().getTypeInfo(), left.getName(), a);
    }
  }

  /**
   * Does the actual check.
   *
   * @param sourceType source of the assoc under test
   * @param targetType target of the assoc under test
   * @param assocName  the associations name
   * @param assoc      association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(OOTypeSymbol sourceType, OOTypeSymbol targetType, String assocName, ASTCDAssociation assoc) {
    // attributes
    Optional<FieldSymbol> conflictingAttribute = sourceType.getFieldList().stream()
        .filter(f -> f.getName().equals(assocName))
        .findAny();

    if (conflictingAttribute.isPresent()) {
      Log.error(String.format("0xCDC62: Association conflicts with the attribute %s in %s.",
          assocName, targetType.getName()),
          assoc.get_SourcePositionStart());
      return false;
    }

    return true;
  }
}
