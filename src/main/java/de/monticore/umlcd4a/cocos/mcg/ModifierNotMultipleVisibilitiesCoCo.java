/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTModifierCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Modifiers may only have none or exactly one visibility.
 *
 * @author Robert Heim
 */
public class ModifierNotMultipleVisibilitiesCoCo implements CD4AnalysisASTModifierCoCo {
  public static final String ERROR_CODE = "0xC4A54";
  
  public static final String ERROR_MSG_FORMAT = "Only none or one visibility is supported, but multiple visibilities were found.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTModifierCoCo#check(de.monticore.umlcd4a._ast.ASTModifier)
   */
  @Override
  public void check(ASTModifier mod) {
    int visibilityCount = 0;
    if (mod.isPrivate()) {
      visibilityCount++;
    }
    if (mod.isProtected()) {
      visibilityCount++;
    }
    if (mod.isPublic()) {
      visibilityCount++;
    }
    if (visibilityCount > 1) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          ERROR_MSG_FORMAT,
          mod.get_SourcePositionStart()));
    }
  }
}
