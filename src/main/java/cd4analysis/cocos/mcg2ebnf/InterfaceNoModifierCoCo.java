/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Interfaces may not have modifiers.
 *
 * @author Robert Heim
 */
public class InterfaceNoModifierCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xCD4AC3005";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s may not have modifiers.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo#check(de.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDInterface node) {
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
