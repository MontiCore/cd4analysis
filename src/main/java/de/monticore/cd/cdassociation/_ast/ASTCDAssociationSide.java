/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation._ast;

public interface ASTCDAssociationSide extends ASTCDAssociationSideTOP {
  default String getName() {
    if (this.isPresentCDRole()) {
      return this.getCDRole().getName();
    }

    // read name from association

    return this.getMCQualifiedName().getClass().getName();
  }
}
