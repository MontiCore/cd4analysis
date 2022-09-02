/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdmerge.validation;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that attribute names start lower-case if not a constant
 */
public class AttributeNameLowerCaseIfNoConstant implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute a) {
    if (!Character.isLowerCase(a.getName().charAt(0)) && !a.getModifier().isFinal()) {
      Log.error(String.format("0xC4A12 Attribute %s must start in lower-case.", a.getName()),
          a.get_SourcePositionStart());
    }
  }

}
