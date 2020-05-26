/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in classes.
 *
 */
public class ClassNoMethodsCoCo implements CD4AnalysisASTCDClassCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (!node.getCDMethodList().isEmpty()) {
      ASTCDMethod method = node.getCDMethodList().get(0);
      Log.error(String.format("0xC4A63 Class %s may not have any methods.", node.getName()),
          method.get_SourcePositionStart());
    }
  }
  
}
