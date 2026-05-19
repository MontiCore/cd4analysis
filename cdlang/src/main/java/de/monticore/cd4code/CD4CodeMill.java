/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code;

import de.monticore.cd4code.types3.CD4CodeTypeCheck3;

public class CD4CodeMill extends CD4CodeMillTOP {
  
  /**
   * Additionally inits the TypeCheck
   */
  public static void init() {
    CD4CodeMillTOP.init();
    CD4CodeTypeCheck3.init();
  }
  
  public static void reset() {
    CD4CodeTypeCheck3.reset();
    CD4CodeMillTOP.reset();
  }
  
}
