/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in interfaces.
 *
 */
public class InterfaceNoMethodsCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDInterfaceCoCo#check(ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (!node.getCDMethodList().isEmpty()) {
      ASTCDMethod method = node.getCDMethodList().get(0);
      Log.error(String.format("0xC4A67 Interface %s may not have methods.", node.getName()),
          method.get_SourcePositionStart());
    }
  }
  
}
