/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Only modifier "derived" is allowed in the ebnf. For Merging Enums with Classes we need "public
 * static final" for the enum constants as well
 */
public class AttributeModifierCoCo implements CDBasisASTCDAttributeCoCo {

  /**
   * @see de.monticore.umlcd4a._cocos.CD4CodeASTCDAttributeCoCo#check(de.monticore.umlcd4a.cd4code._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    ASTModifier actualMod = node.getModifier();
    boolean hasInvalidModifier =
        (actualMod.isAbstract() | actualMod.isFinal() | actualMod.isPrivate()
            | actualMod.isProtected() | actualMod.isPublic() | actualMod.isStatic()
            | actualMod.isPresentStereotype()) && !(actualMod.isPublic() && actualMod.isStatic()
            && actualMod.isFinal());
    if (hasInvalidModifier) {
      Log.error(String.format(
          "0xC4A64 Attribute %s has invalid modifiers. Only derived: \"/\" or 'public static "
              + "final' are permitted.",
          node.getName()), actualMod.get_SourcePositionStart());
    }
  }

}
