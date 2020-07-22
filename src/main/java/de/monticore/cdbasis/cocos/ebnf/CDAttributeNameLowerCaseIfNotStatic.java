/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that attribute names start lower-case.
 */
public class CDAttributeNameLowerCaseIfNotStatic
    implements CDBasisASTCDAttributeCoCo {

  /**
   * @see de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute a) {
    if (!a.getModifier().isStatic() && !Character.isLowerCase(a.getName().charAt(0))) {
      Log.error(String.format("0xCDC03: Attribute %s must start in lower-case.", a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
