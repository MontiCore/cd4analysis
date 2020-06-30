/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;

public class ASTCDAssociation extends ASTCDAssociationTOP {

  public String getPrintableName() {
    if (isPresentName()) {
      return getName();
    }

    return new CDAssociationPrettyPrinter().prettyprint(this);
  }
}
