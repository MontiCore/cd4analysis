/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 *
 * @author Robert Heim
 */
public class EnumImplementOnlyInterfaces extends ImplementOnlyInterfaces implements
    CD4AnalysisASTCDEnumCoCo {

  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    check("enum", node);
  }
}
