/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Interfaces may only be public (or have no modifier).
 *
 * @author Robert Heim
 */
public class InterfaceInvalidModifiersCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xC4A56";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s has invalid modifier %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
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
  
  private void check(boolean invalid, String modifier, ASTModifier mod, ASTCDInterface node) {
    if (invalid) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName(), "\"" + modifier + "\""),
          mod.get_SourcePositionStart()));
    }
  }
}
