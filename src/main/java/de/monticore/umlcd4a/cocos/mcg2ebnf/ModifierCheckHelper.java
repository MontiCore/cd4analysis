/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;

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
    if (modifier.getStereotype().isPresent()) {
      hasModOrStereo |= !modifier.getStereotype().get().getValues().isEmpty();
    }
    return !hasModOrStereo;
  }
  
}
