/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that type names start upper-case.
 */
public class CDClassNameUpperCase implements CDBasisASTCDClassCoCo {

  @Override
  public void check(ASTCDClass a) {
    if (!Character.isUpperCase(a.getName().charAt(0))) {
      Log.error(String.format("0xCDC0A: The first character of the %s %s must be upper-case.",
          CDMill.cDTypeKindPrinter().print(a), a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
