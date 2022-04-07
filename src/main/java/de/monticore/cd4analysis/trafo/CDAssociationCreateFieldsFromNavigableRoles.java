/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.trafo;

import de.monticore.cdassociation._ast.ASTCDRole;

public class CDAssociationCreateFieldsFromNavigableRoles
    extends CDAssociationCreateFieldsFromAllRoles {
  @Override
  public void visit(ASTCDRole node) {
    if (node.getSymbol().isIsDefinitiveNavigable()) {
      super.visit(node);
    }
  }
}
