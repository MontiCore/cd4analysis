/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in enums.
 *
 */
public class EnumNoMethodsCoCo implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (!node.getCDMethodList().isEmpty()) {
      ASTCDMethod method = node.getCDMethodList().get(0);
      Log.error(String.format("0xC4A70 Enum %s may not have methods.", node.getName()),
          method.get_SourcePositionStart());
    }
  }
  
}
