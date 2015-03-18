/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 * @author Robert Heim
 */
public class EnumNoModifierCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xC4A68";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s may not have modifiers.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      ASTModifier emptyMod = ASTModifier.getBuilder().build();
      if (!actualMod.deepEquals(emptyMod)) {
        Log.error(CoCoHelper.buildErrorMsg(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName()),
            actualMod.get_SourcePositionStart()));
      }
    }
  }
  
}
