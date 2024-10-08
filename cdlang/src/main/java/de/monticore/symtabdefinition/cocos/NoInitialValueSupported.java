// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

public class NoInitialValueSupported implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentInitial()) {
      Log.error(
          "0xFDC18 encountered an initial attribute value"
              + " for attribute \""
              + node.getName()
              + "\""
              + " Initial values are not supported.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionStart());
    }
  }
}
