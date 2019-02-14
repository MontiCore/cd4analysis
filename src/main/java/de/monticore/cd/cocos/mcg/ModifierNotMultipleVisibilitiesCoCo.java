/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTModifierCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Modifiers may only have none or exactly one visibility.
 *
 * @author Robert Heim
 */
public class ModifierNotMultipleVisibilitiesCoCo implements CD4AnalysisASTModifierCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTModifierCoCo#check(de.monticore.cd._ast.ASTModifier)
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
      Log.error(
          "0xC4A54 Only none or one visibility is supported, but multiple visibilities were found.",
          mod.get_SourcePositionStart());
    }
  }
}
