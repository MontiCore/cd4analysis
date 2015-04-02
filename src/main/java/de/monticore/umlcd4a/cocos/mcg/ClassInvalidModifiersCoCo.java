/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Classes may not be derived nor static.
 *
 * @author Robert Heim
 */
public class ClassInvalidModifiersCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xC4A53";
  
  public static final String ERROR_MSG_FORMAT = "Class %s has invalid modifier %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getModifier().isPresent()) {
      ASTModifier mod = node.getModifier().get();
      if (mod.isDerived()) {
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName(), "\"derived\""),
            mod.get_SourcePositionStart());
      }
      if (mod.isStatic()) {
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName(), "\"static\""),
            mod.get_SourcePositionStart());
      }
    }
  }
  
}
