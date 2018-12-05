/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cd4analysis._ast;

import static de.monticore.umlcd4a.prettyprint.AstPrinter.EMPTY_STRING;

import java.util.List;
import java.util.Optional;

import de.monticore.types.types._ast.ASTReferenceType;

/**
 * HW super type for classes, interfaces and enums
 *
 * @author Robert Heim, Galina Volkova
 */
public interface ASTCDType extends de.monticore.ast.ASTNode, ASTCD4AnalysisNode {

  String getName();

  Optional<ASTModifier> getModifierOpt();

  void setModifier(ASTModifier modifier);

  List<ASTReferenceType> getInterfaceList();

  List<ASTCDMethod> getCDMethodList();
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   * 
   * @return a string, e.g. abstract private final 
   */
  default String printModifier() {
    Optional<ASTModifier> modifier = getModifierOpt();
    if (!modifier.isPresent()) {
      return EMPTY_STRING;
    }
    
    StringBuilder modifierStr = new StringBuilder();
    if (getModifierOpt().get().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.get().isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.get().isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.get().isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.get().isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.get().isStatic()) {
      modifierStr.append(" static ");
    }
    
    return modifierStr.toString();
  }
  
}
