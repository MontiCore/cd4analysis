/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

public class CDDefinitionNameUpperCase implements CDBasisASTCDDefinitionCoCo {

  @Override
  public void check(ASTCDDefinition cdDefinition) {
    if (!Character.isUpperCase(cdDefinition.getName().charAt(0))) {
      Log.error(String.format("0xCDC0B: First character of the diagram name %s must be upper-case.",
          cdDefinition.getName()),
          cdDefinition.get_SourcePositionStart());
    }
  }
}
