/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that role names start lower-case.
 */
public class CDAssociationRoleNameLowerCase
    implements CDAssociationASTCDAssociationCoCo {

  protected CDAssociationPrettyPrinter prettyPrinter = new CDAssociationPrettyPrinter();

  @Override
  public void check(ASTCDAssociation assoc) {
    check(assoc.getLeft(), assoc);
    check(assoc.getRight(), assoc);
  }

  /**
   * Does the actual check.
   *
   * @param side  association side under test
   * @param assoc association under test
   * @return whether there was an error or not
   */
  private boolean check(ASTCDAssocSide side, ASTCDAssociation assoc) {
    if (side.isPresentCDRole()) {
      return false;
    }
    if (!Character.isLowerCase(side.getCDRole().getName().charAt(0))) {
      Log.error(String.format("0xCDC66 Role %s of association %s must start in lower-case.",
          side.getCDRole().getName(), prettyPrinter.prettyprint(assoc)),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }

}
