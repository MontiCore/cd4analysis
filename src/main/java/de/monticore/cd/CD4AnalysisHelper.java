/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    if (!ast.isPresentModifier()
        || !ast.getModifier().isPresentStereotype()) {
      return false;
    }
    ASTCDStereotype stereotype = ast.getModifier().getStereotype();
    return stereotype.getValueList().stream()
        .filter(v -> v.getName().equals(stereotypeName)).findAny()
        .isPresent();
  }

  public static List<String> getStereotypeValues(ASTCDAttribute ast,
                                                 String stereotypeName) {
    List<String> values = Lists.newArrayList();
    if (ast.isPresentModifier()
        && ast.getModifier().isPresentStereotype()) {
      ast.getModifier().getStereotype().getValueList().stream()
          .filter(value -> value.getName().equals(stereotypeName))
          .filter(value -> value.isPresentValue())
          .forEach(value -> values.add(value.getValue()));
    }
    return values;
  }
  
  public static boolean isAbstract(ASTCDMethod method) {
    return method.getModifier().isAbstract();
  }
  
  public static boolean isAbstract(ASTCDClass clazz) {
    return clazz.isPresentModifier() && clazz.getModifier().isAbstract();
  }
  
  public static List<ASTCDType> getCDTypes(ASTCDDefinition ast) {
    List<ASTCDType> types = new ArrayList<ASTCDType>();
    types.addAll(ast.getCDClassList());
    types.addAll(ast.getCDInterfaceList());
    types.addAll(ast.getCDEnumList());
    return types;
  }
  
  public static List<String> getCDTypeNames(ASTCDDefinition ast) {
    return getCDTypes(ast).stream().map(ASTCDType::getName)
        .collect(Collectors.toList());
  }

}
