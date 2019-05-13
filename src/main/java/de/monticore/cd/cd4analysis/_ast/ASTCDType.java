/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.cd.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd.cd4analysis._ast.ASTModifier;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

/**
 * HW super type for classes, interfaces and enums
 *
 * @author Robert Heim, Galina Volkova
 */
public interface ASTCDType extends ASTCDTypeTOP {

  String getName();

  Optional<ASTModifier> getModifierOpt();

  void setModifier(ASTModifier modifier);

  List<ASTMCObjectType> getInterfaceList();

  List<ASTCDMethod> getCDMethodList();
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   * 
   * @return a string, e.g. abstract private final 
   */
  default String printModifier() {
    return new AstPrinter().printModifier(getModifierOpt());
  }
  
}
