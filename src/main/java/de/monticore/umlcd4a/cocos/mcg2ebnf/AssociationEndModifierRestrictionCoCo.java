/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._ast.ASTStereoValue;
import de.cd4analysis._ast.ASTStereoValueList;
import de.cd4analysis._ast.ASTStereotype;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * On both sides only the stereotype <<ordered>> is allowed, all other
 * modifiers/stereotypes are forbidden.
 *
 * @author Robert Heim
 */
public class AssociationEndModifierRestrictionCoCo implements CD4AnalysisASTCDAssociationCoCo {
  public static final String ERROR_CODE = "0xC4A72";
  
  public static final String ERROR_MSG_FORMAT = "Association ends of association %s may not have modifieres except the stereotype <<ordered>>.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.getLeftModifier().isPresent()) {
      ASTModifier actualMod = node.getLeftModifier().get();
      check(node, actualMod);
    }
    if (node.getRightModifier().isPresent()) {
      ASTModifier actualMod = node.getRightModifier().get();
      check(node, actualMod);
    }
  }
  
  private void check(ASTCDAssociation assoc, ASTModifier actualMod) {
    // allowed is ordered stereotype or completely empty modifier
    
    ASTStereoValueList values = CD4AnalysisNodeFactory.createASTStereoValueList();
    values.add(ASTStereoValue.getBuilder().name("ordered").build());
    ASTStereotype orderedStereo = CD4AnalysisNodeFactory.createASTStereotype(values);
    
    ASTModifier orderedMod = ASTModifier.getBuilder().stereotype(orderedStereo).build();
    ASTModifier emptyMod = ASTModifier.getBuilder().build();
    if (!(actualMod.deepEquals(orderedMod) || actualMod.deepEquals(emptyMod))) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, CD4ACoCoHelper.printAssociation(assoc)),
          actualMod.get_SourcePositionStart()));
    }
    
  }
}
