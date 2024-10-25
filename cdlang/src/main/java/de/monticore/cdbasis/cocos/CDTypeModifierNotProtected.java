/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDTypeCoCo;
import de.se_rwth.commons.logging.Log;

public class CDTypeModifierNotProtected implements CDBasisASTCDTypeCoCo {

  public final static String ERROR_CODE = "0xCDC22";

  @Override
  public void check(ASTCDType node) {
    if (node.getModifier().isProtected()) {
      Log.error(ERROR_CODE + ": Types may not be 'protected'.");
    }
  }
}
