/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDConstructor;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of constructors in classes.
 *
 * @author Robert Heim
 */
public class ClassNoConstructorsCoCo implements CD4AnalysisASTCDClassCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (!node.getCDConstructorList().isEmpty()) {
      ASTCDConstructor constr = node.getCDConstructorList().get(0);
      Log.error(String.format("0xC4A62 Class %s may not have constructors.", node.getName()),
          constr.get_SourcePositionStart());
    }
  }
  
}
