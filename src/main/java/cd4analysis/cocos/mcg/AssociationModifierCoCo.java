/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg;

import cd4analysis.cocos.CD4ACoCoHelper;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures valid modifiers for associations.
 *
 * @author Robert Heim
 */
public class AssociationModifierCoCo implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.cd4analysis._ast.ASTCDAssociation)
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
    Log.error(CoCoHelper.buildErrorMsg("CD-1-44", String.format(
        "The modifier abstract can not be used for associations at association %s.",
        CD4ACoCoHelper.printAssociation(assoc)),
        mod.get_SourcePositionStart()));
  }
}
