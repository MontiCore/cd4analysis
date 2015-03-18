/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures valid modifiers for associations.
 *
 * @author Robert Heim
 */
public class AssociationModifierCoCo implements CD4AnalysisASTCDAssociationCoCo {
  public static final String ERROR_CODE = "0xC4A57";
  
  public static final String ERROR_MSG_FORMAT = "The modifier abstract can not be used for associations at association %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.getLeftModifier().isPresent()) {
      ASTModifier mod = node.getLeftModifier().get();
      if (mod.isAbstract()) {
        error(node, mod);
      }
    }
    if (node.getRightModifier().isPresent()) {
      ASTModifier mod = node.getRightModifier().get();
      if (mod.isAbstract()) {
        error(node, mod);
      }
    }
  }
  
  private void error(ASTCDAssociation assoc, ASTModifier mod) {
    Log.error(CoCoHelper.buildErrorMsg(ERROR_CODE, String.format(
        ERROR_MSG_FORMAT,
        CD4ACoCoHelper.printAssociation(assoc)),
        mod.get_SourcePositionStart()));
  }
}
