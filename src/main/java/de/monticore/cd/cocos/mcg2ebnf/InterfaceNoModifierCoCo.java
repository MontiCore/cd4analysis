/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Interfaces may not have modifiers.
 *
 */
public class InterfaceNoModifierCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.cd._ast.ASTCDClass)
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
