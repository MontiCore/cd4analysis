/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;

public class ASTCDAttribute extends ASTCDAttributeTOP {
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
  public String printType() {
    return new CDBasisFullPrettyPrinter().prettyprint(mCType);
  }
}
