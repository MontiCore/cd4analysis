/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2alloy.cocos;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._cocos.UMLModifierASTModifierCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * CD2Alloy is currently only implemented for abstract modifiers so all others are not allowed
 *
 *
 */
public class OnlyAbstract implements UMLModifierASTModifierCoCo {

  /**
   * @see UMLModifierASTModifierCoCo#check(ASTModifier)
   */
  public void check(ASTModifier node) {
    if(!(node.isAbstract())) {
      Log.error(
          String.format("0xCDD02: %s has invalid modifiers. Only \"abstract\" is permitted.",
              node),
          node.get_SourcePositionStart());
    }
  }
}
