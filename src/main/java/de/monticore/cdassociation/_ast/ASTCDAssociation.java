/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ASTCDAssociation extends ASTCDAssociationTOP {
  protected Optional<SymAssociation> symAssociation;

  public SymAssociation getSymAssociation() {
    if (isPresentSymAssociation()) {
      return this.symAssociation.get();
    }
    Log.error("0xCD001: symAssociation can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public void setSymAssociation(SymAssociation symAssociation) {
    if (symAssociation != null) {
      this.symAssociation = Optional.of(symAssociation);
    }
  }

  public void setSymAssociationAbsent() {
    this.symAssociation = Optional.empty();
  }

  public boolean isPresentSymAssociation() {
    return this.symAssociation.isPresent();
  }

  public String getPrintableName() {
    if (isPresentName()) {
      return getName();
    }

    return new CDAssociationPrettyPrinter().prettyprint(this);
  }
}
