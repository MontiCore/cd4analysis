/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of attributes in enums.
 *
 * @author Galina Volkova
 */
public class EnumNoAttributesCoCo implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (!node.getCDAttributeList().isEmpty()) {
      Log.error(String.format("0xC4A98 Enum %s may not have attributes.", node.getName()),
          node.getCDAttributeList().get(0).get_SourcePositionStart());
    }
  }
  
}
