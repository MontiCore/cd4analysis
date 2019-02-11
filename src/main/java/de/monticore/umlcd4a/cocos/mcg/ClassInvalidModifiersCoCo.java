/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Classes may not be derived nor static.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class ClassInvalidModifiersCoCo implements CD4AnalysisASTCDClassCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.isPresentModifier()) {
      ASTModifier mod = node.getModifier();
      if (mod.isDerived() || mod.isStatic()) {
        Log.error(
            String.format("0xC4A53 Class %s has invalid modifier %s.", node.getName(),
                mod.isDerived() ? "\"derived\"" : "\"static\""),
            mod.get_SourcePositionStart());
      }
    }
  }
  
}
