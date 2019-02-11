/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures valid modifiers for associations.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class AssociationModifierCoCo implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
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
