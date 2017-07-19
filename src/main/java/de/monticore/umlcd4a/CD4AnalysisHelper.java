/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotype;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
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
  
  public static List<ASTCDType> getCDTypes(ASTCDDefinition ast) {
    List<ASTCDType> types = new ArrayList<ASTCDType>();
    types.addAll(ast.getCDClasses());
    types.addAll(ast.getCDInterfaces());
    types.addAll(ast.getCDEnums());
    return types;
  }
  
  public static List<String> getCDTypeNames(ASTCDDefinition ast) {
    return getCDTypes(ast).stream().map(ASTCDType::getName)
        .collect(Collectors.toList());
  }

}
