/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "abstract" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class ClassModifierOnlyAbstractCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xCD4AC3001";
  
  public static final String ERROR_MSG_FORMAT = "Class %s has invalid modifiers. Only \"abstract\" is permitted.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo#check(de.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      ASTModifier abstractMod = ASTModifier.getBuilder().r_abstract(true).build();
      ASTModifier emptyMod = ASTModifier.getBuilder().build();
      if (!(actualMod.deepEquals(abstractMod) || actualMod.deepEquals(emptyMod))) {
        Log.error(CoCoHelper.buildErrorMsg(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, node.getName()),
            actualMod.get_SourcePositionStart()));
      }
    }
  }
}
