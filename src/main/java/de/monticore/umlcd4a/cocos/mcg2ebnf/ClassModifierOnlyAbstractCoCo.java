/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "abstract" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class ClassModifierOnlyAbstractCoCo implements CD4AnalysisASTCDClassCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getModifier().isPresent()) {
      ASTModifier actualMod = node.getModifier().get();
      boolean hasInvalidModifier = actualMod.isDerived()
          | actualMod.isFinal()
          | actualMod.isPrivate()
          | actualMod.isProtected()
          | actualMod.isPublic()
          | actualMod.isStatic()
          | actualMod.getStereotype().isPresent();
      if (hasInvalidModifier) {
        Log.error(String.format(
            "0xC4A61 Class %s has invalid modifiers. Only \"abstract\" is permitted.",
            node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
