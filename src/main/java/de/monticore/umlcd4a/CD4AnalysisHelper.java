/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.List;

import com.google.common.collect.Lists;

import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDMethod;
import de.monticore.umlcd4a._ast.ASTStereotype;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
// TODO: check implementations
public class CD4AnalysisHelper {
  
  public static boolean hasStereotype(ASTCDAttribute ast,
      String stereotypeName) {
    if (!ast.getModifier().isPresent()
        || !ast.getModifier().get().getStereotype().isPresent()) {
      return false;
    }
    ASTStereotype stereotype = ast.getModifier().get().getStereotype()
        .get();
    return stereotype.getValues().stream()
        .filter(v -> v.getName().equals(stereotypeName)).findAny()
        .isPresent();
  }

  public static List<String> getStereotypeValues(ASTCDAttribute ast,
      String stereotypeName) {
    List<String> values = Lists.newArrayList();
    if (ast.getModifier().isPresent()
        && ast.getModifier().get().getStereotype().isPresent()) {
      ast.getModifier().get().getStereotype().get().getValues().stream()
          .filter(value -> value.getName().equals(stereotypeName))
          .filter(value -> value.getValue().isPresent())
          .forEach(value -> values.add(value.getValue().get()));
    }
    return values;
  }
  
  public static boolean isAbstract(ASTCDMethod method) {
    return method.getModifier().isAbstract();
  }
  
  public static boolean isAbstract(ASTCDClass clazz) {
    return clazz.getModifier().isPresent() && clazz.getModifier().get().isAbstract();
  }
  
}
