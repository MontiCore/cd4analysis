/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.ast.ASTNode;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotype;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * On both sides only the stereotype <<ordered>> is allowed, all other
 * modifiers/stereotypes are forbidden.
 *
 * @author Robert Heim
 */
public class AssociationEndModifierRestrictionCoCo implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.isPresentLeftModifier()) {
      ASTModifier actualMod = node.getLeftModifier();
      check(node, actualMod);
    }
    if (node.isPresentRightModifier()) {
      ASTModifier actualMod = node.getRightModifier();
      check(node, actualMod);
    }
  }
  
  private void check(ASTCDAssociation assoc, ASTModifier actualMod) {
    if (!ModifierCheckHelper.isEmptyModifier(actualMod)) {
      error(assoc, actualMod);
    }
    
    if (actualMod.isPresentStereotype()) {
      ASTStereotype stereo = actualMod.getStereotype();
      for (ASTStereoValue val : stereo.getValueList()) {
        if (!"ordered".equals(val.getName())) {
          error(assoc, val);
        }
      }
    }
    
  }
  
  private void error(ASTCDAssociation assoc, ASTNode node) {
    Log.error(
        String
            .format(
                "0xC4A72 Association ends of association %s may not have modifieres except the stereotype <<ordered>>.",
                CD4ACoCoHelper.printAssociation(assoc)),
        node.get_SourcePositionStart());
  }
}
