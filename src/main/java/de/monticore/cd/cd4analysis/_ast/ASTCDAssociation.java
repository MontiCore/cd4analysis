/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  private Optional<CDAssociationSymbol> leftToRightSymbol = Optional.empty();
  private Optional<CDAssociationSymbol> rightToLeftSymbol = Optional.empty();

  public ASTCDAssociation() {
  }

  @Override
  public String toString() {
    IndentPrinter ip = new IndentPrinter();
    this.accept(new CDPrettyPrinter(ip));
    return ip.getContent().replace("\r", "").replace("\n", "");
  }

  public CDAssociationSymbol getLeftToRightSymbol() {
    if (leftToRightSymbol.isPresent()) {
      return leftToRightSymbol.get();
    } else {
      Log.error("0xU6004 LeftToRightSymbol can't return a value. It is empty.");
      // Normally this statement is not reachable
      throw new IllegalStateException();
    }
  }

  public boolean isPresentLeftToRightSymbol() {
    return leftToRightSymbol.isPresent();
  }

  public void setLeftToRightSymbol(CDAssociationSymbol leftToRightSymbol) {
    this.leftToRightSymbol = Optional.ofNullable(leftToRightSymbol);
  }

  public void setLeftToRightSymbolAbsent() {
    this.leftToRightSymbol = Optional.empty();
  }

  public CDAssociationSymbol getRightToLeftSymbol() {
    if (rightToLeftSymbol.isPresent()) {
      return rightToLeftSymbol.get();
    } else {
      Log.error("0xU6005 RightToLeftSymbol can't return a value. It is empty.");
      // Normally this statement is not reachable
      throw new IllegalStateException();
    }
  }

  public void setRightToLeftSymbol(CDAssociationSymbol rightToLeftSymbol) {
    this.rightToLeftSymbol = Optional.ofNullable(rightToLeftSymbol);
  }

  public boolean isPresentRightToLeftSymbol() {
    return rightToLeftSymbol.isPresent();
  }

  public void setRightToLeftSymbolAbsent() {
    this.rightToLeftSymbol = Optional.empty();
  }

  public boolean isReadOnly() {
    return isPresentReadOnly();
  }

  public void setReadOnly(boolean isReadOnly) {
    if (isReadOnly) {
      this.setReadOnly("read-only");
    } else {
      this.setReadOnly((String) null);
    }
  }

  public String getName() {
    return isPresentName() ? this.name.get() : "";
  }
}
