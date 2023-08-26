/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

public class CDAttributeInInterfaceInitialized implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  public static final String ERROR_CODE = "0xCDCF8";

  public static final String ERROR_MSG = " attributes in interfaces must be initialized %s";

  @Override
  public void check(ASTCDInterface node) {
    for (ASTCDAttribute attribute : node.getCDAttributeList()) {
      if (!attribute.isPresentInitial()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG, attribute.get_SourcePositionStart()));
      }
    }
  }
}
