/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;

public interface ASTCDAssocSide extends ASTCDAssocSideTOP {
  default String getName() {
    if (this.isPresentCDRole()) {
      return this.getCDRole().getName();
    }

    return new CDBasisFullPrettyPrinter().prettyprint(this.getMCQualifiedType());
  }

  /**
   * get the name of the association side use the information of the role and association to get a
   * name, use the following order
   *
   * <pre>
   * 1. use role name (if present)
   * 2. use association name (if present)
   * 3. use classname (uncap first)
   * </pre>
   *
   * @param assoc the association to calculate the name from
   * @return the name of the association with the described logic
   */
  default String getName(ASTCDAssociation assoc) {
    if (this.isPresentCDRole()) {
      return this.getCDRole().getName();
    }

    // read name from association
    if (assoc.isPresentName()) {
      return assoc.getName();
    }

    return new CDBasisFullPrettyPrinter().prettyprint(this.getMCQualifiedType());
  }

  default boolean isPresentSymbol() {
    return isPresentCDRole() && getCDRole().isPresentSymbol();
  }

  default CDRoleSymbol getSymbol() {
    return getCDRole().getSymbol();
  }

  boolean isLeft();
}
