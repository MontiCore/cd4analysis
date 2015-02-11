/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 * @author Robert Heim
 */
public class EnumNoModifierCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xCD4AC3008";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s may not have modifiers.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo#check(de.cd4analysis._ast.ASTCDEnum)
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
