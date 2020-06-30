/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;

public interface ASTCDAssocSide extends ASTCDAssocSideTOP {
  default String getName() {
    if (this.isPresentCDRole()) {
      return this.getCDRole().getName();
    }

    // read name from association

    return new CDBasisPrettyPrinterDelegator().prettyprint(this.getMCQualifiedType());
  }
}
