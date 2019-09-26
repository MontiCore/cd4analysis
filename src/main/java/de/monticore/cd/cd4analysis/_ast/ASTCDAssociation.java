/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  private CDAssociationSymbol leftToRightSymbol;
  private CDAssociationSymbol rightToLeftSymbol;

  public ASTCDAssociation() {
  }

  @Override
  public String toString() {
    IndentPrinter ip = new IndentPrinter();
    this.accept(new CDPrettyPrinter(ip));
    return ip.getContent().replace("\r","").replace("\n", "");
  }

  public Optional<CDAssociationSymbol> getLeftToRightSymbol() {
    return Optional.ofNullable(leftToRightSymbol);
  }

  public void setLeftToRightSymbol(CDAssociationSymbol leftToRightSymbol) {
    this.leftToRightSymbol = leftToRightSymbol;
  }

  public Optional<CDAssociationSymbol> getRightToLeftSymbol() {
    return Optional.ofNullable(rightToLeftSymbol);
  }

  public void setRightToLeftSymbol(CDAssociationSymbol rightToLeftSymbol) {
    this.rightToLeftSymbol = rightToLeftSymbol;
  }

  public boolean isReadOnly() {
    return getReadOnlyOpt().isPresent();
  }

  public void setReadOnly(boolean isReadOnly) {
    if (isReadOnly) {
      this.setReadOnly("read-only");
    }
    else {
      this.setReadOnly((String)null);
    }
  }

  public String getName() {
   return getNameOpt().orElse("");
  }
}
