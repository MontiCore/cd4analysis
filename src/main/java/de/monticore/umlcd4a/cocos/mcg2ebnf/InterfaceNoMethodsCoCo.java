/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDMethod;
import de.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoMethodsCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xC4A67";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s may not have methods.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.getCDMethods().size() > 0) {
      ASTCDMethod method = node.getCDMethods().get(0);
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          method.get_SourcePositionStart()));
    }
  }
  
}
