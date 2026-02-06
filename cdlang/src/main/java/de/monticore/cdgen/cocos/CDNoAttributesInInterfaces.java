/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

public class CDNoAttributesInInterfaces implements CD4CodeBasisASTCDInterfaceCoCo {

  public static final String ERROR_CODE = "0xCDCE3";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Interface %s must not have attributes.";

  @Override
  public void check(ASTCDInterface node) {
    if (node.getCDAttributeList() != null && !node.getCDAttributeList().isEmpty()) {
      Log.error(String.format(ERROR_MESSAGE, node.getName()), node.get_SourcePositionStart());
    }
  }

}
