/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that derived attributes are not initialized.
 */
// TODO SVa: should this be allowed, a default method implementation could be used
public class CDTypeNoInitializationOfDerivedAttribute
    implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isDerived()) {
      if (attr.isPresentInitial()) {
        Log.error(String.format("0xCDC0C: Invalid initialization of the derived attribute %s. Derived attributes may not be initialized.", attr.getName()),
            attr.get_SourcePositionStart());
      }
    }
  }
}
