/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 * @author Robert Heim
 */
public class EnumNoModifierCoCo implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.isPresentModifier()) {
      ASTModifier actualMod = node.getModifier();
      if (!ModifierCheckHelper.isEmptyModifierAndNoStereo(actualMod)) {
        Log.error(String.format("0xC4A68 Enum %s may not have modifiers.", node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
