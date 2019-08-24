/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDConstructor;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of constructors in classes.
 *
 * @author Robert Heim
 */
public class EnumNoConstructorsCoCo implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (!node.getCDConstructorList().isEmpty()) {
      ASTCDConstructor constr = node.getCDConstructorList().get(0);
      Log.error(String.format("0xC4A69 Enum %s may not have constructors.", node.getName()),
          constr.get_SourcePositionStart());
    }
  }
  
}
