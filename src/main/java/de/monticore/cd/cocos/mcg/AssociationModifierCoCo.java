/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures valid modifiers for associations.
 *
 */
public class AssociationModifierCoCo implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.cd._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.isPresentLeftModifier()) {
      ASTModifier mod = node.getLeftModifier();
      if (mod.isAbstract()) {
        error(node, mod);
      }
    }
    if (node.isPresentRightModifier()) {
      ASTModifier mod = node.getRightModifier();
      if (mod.isAbstract()) {
        error(node, mod);
      }
    }
  }
  
  private void error(ASTCDAssociation assoc, ASTModifier mod) {
    Log.error(String.format(
        "0xC4A57 The modifier abstract can not be used for associations at association %s.",
        CD4ACoCoHelper.printAssociation(assoc)),
        mod.get_SourcePositionStart());
  }
}
