/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoMethodsCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (!node.getCDMethods().isEmpty()) {
      ASTCDMethod method = node.getCDMethods().get(0);
      Log.error(String.format("0xC4A67 Interface %s may not have methods.", node.getName()),
          method.get_SourcePositionStart());
    }
  }
  
}
