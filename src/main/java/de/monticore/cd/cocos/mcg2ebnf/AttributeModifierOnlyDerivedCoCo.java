/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "derived" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class AttributeModifierOnlyDerivedCoCo implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentModifier()) {
      ASTModifier actualMod = node.getModifier();
      boolean hasInvalidModifier = actualMod.isAbstract()
          | actualMod.isFinal()
          | actualMod.isPrivate()
          | actualMod.isProtected()
          | actualMod.isPublic()
          | actualMod.isStatic()
          | actualMod.isPresentStereotype();
      if (hasInvalidModifier) {
        Log.error(
            String.format("0xC4A64 Attribute %s has invalid modifiers. Only \"/\" is permitted.",
                node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
