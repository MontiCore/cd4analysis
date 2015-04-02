/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo;

/**
 * Interfaces may not have modifiers.
 *
 * @author Robert Heim
 */
public class InterfaceNoModifierCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xC4A65";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s may not have modifiers.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      ASTModifier emptyMod = ASTModifier.getBuilder().build();
      if (!actualMod.deepEquals(emptyMod)) {
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
  
}
