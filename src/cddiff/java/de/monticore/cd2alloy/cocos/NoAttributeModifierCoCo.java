/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2alloy.cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks, if all classes have no attribute modifier.
 */
public class NoAttributeModifierCoCo implements CDBasisASTCDAttributeCoCo {
  /**
   * @see CDBasisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  public void check(ASTCDAttribute node) {
    ASTModifier actualMod = node.getModifier();
    boolean hasInvalidModifier = actualMod.isAbstract()
            | actualMod.isFinal()
            | actualMod.isPrivate()
            | actualMod.isProtected()
            | actualMod.isDerived()
            | actualMod.isPublic()
            | actualMod.isStatic()
            | actualMod.isPresentStereotype();
    if (hasInvalidModifier) {
      // In current MontiCore warning this just works with warnings and
      // not with errors, because of a FIXME in the error case
      Log.warn(
              String.format(
                      "0xCDD13 Attribute %s has invalid modifiers. No modifiers are allowed for "
                          + "CD4Analysis.",
                      node.getName()),
              actualMod.get_SourcePositionStart());
    }
  }
}
