/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo;

/**
 * Enums may only be public (or have no modifier).
 *
 * @author Robert Heim
 */
public class EnumInvalidModifiersCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xC4A55";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s has invalid modifier %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.getModifier().isPresent()) {
      ASTModifier mod = node.getModifier().get();
      check(mod.isAbstract(), "abstract", mod, node);
      check(mod.isDerived(), "derived", mod, node);
      check(mod.isFinal(), "final", mod, node);
      check(mod.isPrivate(), "private", mod, node);
      check(mod.isProtected(), "protected", mod, node);
      check(mod.isStatic(), "static", mod, node);
    }
  }
  
  private void check(boolean invalid, String modifier, ASTModifier mod, ASTCDEnum node) {
    if (invalid) {
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName(), "\"" + modifier + "\""),
          mod.get_SourcePositionStart());
    }
  }
}
