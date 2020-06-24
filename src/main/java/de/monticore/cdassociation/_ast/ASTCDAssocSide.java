/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

public interface ASTCDAssocSide extends ASTCDAssocSideTOP {
  default String getName() {
    if (this.isPresentCDRole()) {
      return this.getCDRole().getName();
    }

    // read name from association

    return this.getMCQualifiedName().getBaseName();
  }
}
