/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 *
 */
public class ClassImplementOnlyInterfaces extends ImplementOnlyInterfaces implements
    CD4AnalysisASTCDClassCoCo {

  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    check("class", node);
  }
}
