/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.trafo;

import static de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo.createASTCDRoleIfAbsent;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;

public class CDAssociationRoleNameTrafo implements CDAssociationVisitor2 {

  @Override
  public void visit(ASTCDAssociation node) {
    createASTCDRoleIfAbsent(node);
  }
}
