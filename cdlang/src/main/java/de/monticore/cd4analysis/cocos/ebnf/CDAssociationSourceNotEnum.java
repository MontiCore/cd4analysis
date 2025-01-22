/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

/** Checks that type of the type-qualifier of an type-qualified association exists. */
// TODO should an enum be allowed?
public class CDAssociationSourceNotEnum implements CDAssociationASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation node) {
    if (node.getCDAssocDir().isDefinitiveNavigableLeft()) {
      check(node.getRight(), node);
    }
    if (node.getCDAssocDir().isDefinitiveNavigableRight()) {
      check(node.getLeft(), node);
    }
  }

  /**
   * Does the actual check.
   *
   * @param side the association side whose type may not be an enum
   * @param node the association under test
   */
  private void check(ASTCDAssocSide side, ASTCDAssociation node) {
    SymTypeExpression type = side.getSymbol().getType();
    if (type.hasTypeInfo() && CoCoHelper.isEnum(type.getTypeInfo())) {
      Log.error(
          String.format(
              "0xCDC67: Association %s is invalid, because an association's source may not be an Enumeration.",
              CDAssociationMill.prettyPrint(node, false)),
          node.get_SourcePositionStart());
    }
  }
}
