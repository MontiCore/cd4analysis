/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Interfaces may not have modifiers.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class InterfaceNoModifierCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.isPresentModifier()) {
      ASTModifier actualMod = node.getModifier();
      if (!ModifierCheckHelper.isEmptyModifierAndNoStereo(actualMod)) {
        Log.error(String.format("0xC4A65 Interface %s may not have modifiers.", node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
  
}
