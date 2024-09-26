/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cdbasis._symboltable.ICDBasisScope;

public class ASTCDAttribute extends ASTCDAttributeTOP {

  // TODO: eigentlich sollte das hier überflüssig sein
  @Override
  public void setEnclosingScope(ICDBasisScope enclosingScope) {
    super.setEnclosingScope(enclosingScope);
    this.getMCType().setEnclosingScope(enclosingScope);
  }

  /**
   * Prints the attribute type as a String.
   *
   * @return The attribute type as String
   */
  @Deprecated(forRemoval = true)
  public String printType() {
    return getMCType().printType();
  }
}
