/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "abstract" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class ClassModifierOnlyAbstractCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xC4A61";
  
  public static final String ERROR_MSG_FORMAT = "Class %s has invalid modifiers. Only \"abstract\" is permitted.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      ASTModifier abstractMod = ASTModifier.getBuilder().r_abstract(true).build();
      ASTModifier emptyMod = ASTModifier.getBuilder().build();
      if (!(actualMod.deepEquals(abstractMod) || actualMod.deepEquals(emptyMod))) {
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
