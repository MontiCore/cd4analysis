/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg;

import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may only be public (or have no modifier).
 *
 * @author Robert Heim
 */
public class EnumInvalidModifiersCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xCD4AC2005";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s has invalid modifier %s.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo#check(de.cd4analysis._ast.ASTCDEnum)
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
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName(), "\"" + modifier + "\""),
          mod.get_SourcePositionStart()));
    }
  }
}
