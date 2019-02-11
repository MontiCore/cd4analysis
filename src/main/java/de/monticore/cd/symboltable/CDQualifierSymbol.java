/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;

/**
 * Represents a cd qualifier.
 *
 * @author Robert Heim
 */
public class CDQualifierSymbol extends CommonSymbol {
  public static final CDQualifierSymbolKind KIND = new CDQualifierSymbolKind();
  
  private boolean isTypeQualifier = false;
  
  private boolean isNameQualifier = false;
  
  /**
   * Constructs a name qualifier
   * 
   * @param name
   * @see #CDQualifierSymbol(ASTType)
   */
  public CDQualifierSymbol(String name) {
    super(name, KIND);
    setNameQualifier(true);
  }
  
  /**
   * Constructs a type qualifier
   * 
   * @param type
   * @see #CDQualifierSymbol(String)
   */
  public CDQualifierSymbol(ASTType type) {
    super(TypesPrinter.printType(type), KIND);
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
