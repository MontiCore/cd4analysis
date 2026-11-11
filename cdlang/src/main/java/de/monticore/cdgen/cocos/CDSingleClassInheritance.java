/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

public class CDSingleClassInheritance implements CDBasisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0xCDCE4";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Class %s extends more than one class which is not allowed in Java.";
  
  @Override
  public void check(ASTCDClass node) {
    if (node.getSuperclassList().size() > 1) {
      Log.error(String.format(ERROR_MESSAGE, node.getName()), node.get_SourcePositionStart());
    }
  }
  
}
