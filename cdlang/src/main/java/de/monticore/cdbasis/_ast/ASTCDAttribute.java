/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.prettyprint.IndentPrinter;

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
  @Deprecated
  public String printType() {
    return new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(mCType);
  }
}
