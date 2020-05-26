/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 */
public class EnumNoModifierCoCo implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.isPresentModifier()) {
      ASTModifier actualMod = node.getModifier();
      if (!ModifierCheckHelper.isEmptyModifierAndNoStereo(actualMod)) {
        Log.error(String.format("0xC4A68 Enum %s may not have modifiers.", node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
