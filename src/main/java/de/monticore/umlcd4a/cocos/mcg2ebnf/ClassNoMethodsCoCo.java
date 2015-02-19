/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDMethod;
import de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in classes.
 *
 * @author Robert Heim
 */
public class ClassNoMethodsCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xCD4AC3003";
  
  public static final String ERROR_MSG_FORMAT = "Class %s may not have any methods.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getCDMethods().size() > 0) {
      ASTCDMethod method = node.getCDMethods().get(0);
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          method.get_SourcePositionStart()));
    }
  }
  
}
