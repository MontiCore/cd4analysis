/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition;

import de.monticore.symtabdefinition.types3.SymTabDefinitionTypeCheck3;

public class SymTabDefinitionMill extends SymTabDefinitionMillTOP {
  
  /**
   * Additionally inits the TypeCheck
   */
  public static void init() {
    SymTabDefinitionMillTOP.init();
    SymTabDefinitionTypeCheck3.init();
  }
  
  public static void reset() {
    SymTabDefinitionTypeCheck3.reset();
    SymTabDefinitionMillTOP.reset();
  }
  
}
