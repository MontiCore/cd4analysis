/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashSet;

/**
 * Checks uniqueness among the enum constants.
 */
public class CDEnumConstantUnique implements CDInterfaceAndEnumASTCDEnumCoCo {

  @Override
  public void check(ASTCDEnum node) {
    Collection<String> usedNames = new HashSet<>();
    for (ASTCDEnumConstant constant : node.getCDEnumConstantsList()) {
      String name = constant.getName();
      if (usedNames.contains(name)) {
        Log.error(String.format("0xCDC30: Duplicate enum constant: %s.", name),
            constant.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
