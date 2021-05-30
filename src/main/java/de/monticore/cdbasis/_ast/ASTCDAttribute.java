/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._symboltable.ICDBasisScope;

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
    return new CD4CodeFullPrettyPrinter().prettyprint(mCType);
  }

}
