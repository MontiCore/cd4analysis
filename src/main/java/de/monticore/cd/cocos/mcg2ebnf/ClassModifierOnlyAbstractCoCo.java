/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "abstract" is allowed in the ebnf.
 *
 * @author Robert Heim
 */
public class ClassModifierOnlyAbstractCoCo implements CD4AnalysisASTCDClassCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.isPresentModifier()) {
      ASTModifier actualMod = node.getModifier();
      boolean hasInvalidModifier = actualMod.isDerived()
          | actualMod.isFinal()
          | actualMod.isPrivate()
          | actualMod.isProtected()
          | actualMod.isPublic()
          | actualMod.isStatic()
          | actualMod.isPresentStereotype();
      if (hasInvalidModifier) {
        Log.error(String.format(
            "0xC4A61 Class %s has invalid modifiers. Only \"abstract\" is permitted.",
            node.getName()),
            actualMod.get_SourcePositionStart());
      }
    }
  }
}
