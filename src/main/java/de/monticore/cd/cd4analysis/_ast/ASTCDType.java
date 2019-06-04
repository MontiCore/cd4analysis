/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
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

  void setSymbol2(CDTypeSymbol symbol);

  public  de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol getSymbol2 ();

  public  Optional<de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol> getSymbol2Opt ();

  void setCDTypeSymbol(CDTypeSymbol symbol);

  void setEnclosingScope2(ICD4AnalysisScope enclosingScope);

  void setSpannedScope2(ICD4AnalysisScope spannedScope);
}
