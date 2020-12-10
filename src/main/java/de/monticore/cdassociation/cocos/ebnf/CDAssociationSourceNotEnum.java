/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that type of the type-qualifier of an type-qualified association
 * exists.
 */
public class CDAssociationSourceNotEnum
    implements CDAssociationASTCDAssociationCoCo {

  protected final CDAssociationPrettyPrinter prettyPrinter = new CDAssociationPrettyPrinter();

  @Override
  public void check(ASTCDAssociation node) {
    check(node.getLeft(), node);
    check(node.getRight(), node);
  }

  /**
   * Does the actual check.
   *
   * @param side the association side whose type may not be an enum
   * @param node the association under test
   */
  private void check(ASTCDAssocSide side, ASTCDAssociation node) {
    if (CoCoHelper.isEnum(side.getSymbol().getType().getTypeInfo())) {
      Log.error(
          String
              .format(
                  "0xCDC67: Association %s is invalid, because an association's source may not be an Enumeration.",
                  prettyPrinter.prettyprint(node)),
          node.get_SourcePositionStart());
    }
  }

}
