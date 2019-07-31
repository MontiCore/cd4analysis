/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

/**
 * Represents a cd qualifier.
 *
 * @author Robert Heim
 */
public class CDQualifierSymbol extends CDQualifierSymbolTOP {

  private boolean isTypeQualifier = false;
  
  private boolean isNameQualifier = false;
  
  /**
   * Constructs a name qualifier
   * 
   * @param name
   */
  public CDQualifierSymbol(String name) {
    super(name);
    setNameQualifier(true);
  }
  
  /**
   * Constructs a type qualifier
   * 
   * @param type
   * @see #CDQualifierSymbol(String)
   */
  public CDQualifierSymbol(ASTMCType type) {
    super(new AstPrinter().printType(type));
    setTypeQualifier(true);
  }
  
  /**
   * @return isTypeQualifier
   */
  public boolean isTypeQualifier() {
    return this.isTypeQualifier;
  }
  
  /**
   * @param isTypeQualifier the isTypeQualifier to set
   */
  public void setTypeQualifier(boolean isTypeQualifier) {
    this.isTypeQualifier = isTypeQualifier;
  }
  
  /**
   * @return isNameQualifier
   */
  public boolean isNameQualifier() {
    return this.isNameQualifier;
  }
  
  /**
   * @param isNameQualifier the isNameQualifier to set
   */
  public void setNameQualifier(boolean isNameQualifier) {
    this.isNameQualifier = isNameQualifier;
  }
  
}
