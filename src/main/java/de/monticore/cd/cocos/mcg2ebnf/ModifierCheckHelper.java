/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTModifier;

/**
 * Helper providing common methods to check some modifier related constraints.
 *
 * @author Robert Heim
 */
public class ModifierCheckHelper {
  /**
   * Checks that the modifier is empty (but might have a stereotype)
   * 
   * @return
   */
  public static boolean isEmptyModifier(ASTModifier modifier) {
    boolean hasMod = modifier.isAbstract()
        | modifier.isDerived()
        | modifier.isFinal()
        | modifier.isPrivate()
        | modifier.isProtected()
        | modifier.isPublic()
        | modifier.isStatic();
    return !hasMod;
  }
  
  /**
   * Checks that the modifier is empty (and has no stereotype)
   * 
   * @return
   */
  public static boolean isEmptyModifierAndNoStereo(ASTModifier modifier) {
    boolean hasModOrStereo = !isEmptyModifier(modifier);
    if (modifier.isPresentStereotype()) {
      hasModOrStereo |= !modifier.getStereotype().getValueList().isEmpty();
    }
    return !hasModOrStereo;
  }
  
}