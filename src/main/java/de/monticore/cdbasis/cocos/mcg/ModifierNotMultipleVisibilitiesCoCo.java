/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.mcg;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._cocos.UMLModifierASTModifierCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Modifiers may only have none or exactly one visibility.
 */
public class ModifierNotMultipleVisibilitiesCoCo
    implements UMLModifierASTModifierCoCo {

  /**
   * @see UMLModifierASTModifierCoCo#check(ASTModifier)
   */
  @Override
  public void check(ASTModifier mod) {
    int visibilityCount = 0;
    if (mod.isPrivate()) {
      visibilityCount++;
    }
    if (mod.isProtected()) {
      visibilityCount++;
    }
    if (mod.isPublic()) {
      visibilityCount++;
    }
    if (visibilityCount > 1) {
      Log.error(
          "0xCDC10:Only none or one visibility is supported, but multiple visibilities were found.",
          mod.get_SourcePositionStart());
    }
  }
}
