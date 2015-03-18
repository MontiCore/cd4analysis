/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDMethod;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of methods in classes.
 *
 * @author Robert Heim
 */
public class ClassNoMethodsCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xC4A63";
  
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
