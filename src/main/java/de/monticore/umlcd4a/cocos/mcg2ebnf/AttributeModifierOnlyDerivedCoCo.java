/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo;

/**
 * Only modifier "derived" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class AttributeModifierOnlyDerivedCoCo implements CD4AnalysisASTCDAttributeCoCo {
  public static final String ERROR_CODE = "0xC4A64";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s has invalid modifiers. Only \"/\" is permitted.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      ASTModifier derivedMod = ASTModifier.getBuilder().derived(true).build();
      ASTModifier emptyMod = ASTModifier.getBuilder().build();
      if (!(actualMod.deepEquals(derivedMod) || actualMod.deepEquals(emptyMod))) {
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
